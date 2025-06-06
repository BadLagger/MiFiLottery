package com.example.lottery.service;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketInInvoiceDto;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Invoice;
import com.example.lottery.entity.Ticket;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.mapper.InvoiceMapper;
import com.example.lottery.mapper.JsonMapper;
import com.example.lottery.repository.InvoiceRepository;
import com.example.lottery.service.Impl.UserSelectedTicketGenerator;
import com.example.lottery.service.utils.PaymentLinkGenerator;
import com.example.lottery.service.validator.Validator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {
  private final InvoiceRepository invoiceRepository;
  private final InvoiceMapper invoiceMapper;
  private final DrawService drawService;
  private final PaymentLinkGenerator paymentLinkGenerator;
  private final Validator validator;
  private final TicketsFactory ticketsFactory;
  private final TicketService ticketService;

  public TicketInInvoiceDto createInvoice(TicketCreateDto dto, Long userId) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime now = LocalDateTime.now();
    Draw draw = getDrawById(dto.getDrawId());

    validator.validateForBuyingByDraw(draw);

    // Валидируем выбранные номера от пользователя
    TicketGenerator generator = ticketsFactory.getGenerator(draw);
    if (generator instanceof UserSelectedTicketGenerator) {
      validator.validateNumbers(dto.getNumbers(), draw);
    } else {
      List<Integer> numbers = generator.generateTicket().getNumbers();
      dto.setNumbers(numbers);
    }

    // Собираем данные для инвойса
    InvoiceDto invoiceDto = new InvoiceDto();
    invoiceDto.setUserId(userId);
    invoiceDto.setRegisterTime(now);
    invoiceDto.setTicketData(JsonMapper.toJson(dto));
    invoiceDto.setStatus(Invoice.Status.UNPAID);

    TicketInInvoiceDto ticketInInvoice = new TicketInInvoiceDto();
    ticketInInvoice.setUserId(userId);
    ticketInInvoice.setDrawId(dto.getDrawId());
    ticketInInvoice.setRegisterTime(now.format(formatter));
    ticketInInvoice.setNumbers(dto.getNumbers());
    ticketInInvoice.setTicketPrice(drawService.getTicketPriceByDrawId(dto.getDrawId()));
    ticketInInvoice.setPaymentLink(paymentLinkGenerator.generatePaymentLink(ticketInInvoice));

    invoiceDto.setPaymentLink(ticketInInvoice.getPaymentLink());
    Invoice savedInvoice = invoiceRepository.save(invoiceMapper.toEntity(invoiceDto));
    ticketInInvoice.setInvoiceId(savedInvoice.getId());

    return ticketInInvoice;
  }

  public List<InvoiceDto> getInvoicesByStatus(Invoice.Status status, Long userId) {
    return invoiceRepository.findByStatusAndUser_Id(status, userId).stream()
        .map((invoiceMapper::toDto))
        .collect(Collectors.toList());
  }

  public InvoiceDto getInvoiceById(Long id, Long userId) {
    return invoiceMapper.toDto(getInvoice(id, userId));
  }

  public void setPending(Long id, Long userId) {
    Invoice invoice = getInvoice(id, userId);

    isInvoiceNotCancelled(invoice);
    if (invoice.getStatus() != Invoice.Status.UNPAID) {
      throw new ValidationException("Только неоплаченные инвойсы можно оплатить");
    }

    invoice.setStatus(Invoice.Status.PENDING);
    invoiceRepository.save(invoice);
  }

  @Transactional
  public void setPaid(Long id) {
    Invoice invoice = getInvoice(id);

    isInvoiceNotCancelled(invoice);
    if (invoice.getStatus() == Invoice.Status.PENDING) {
      invoice.setStatus(Invoice.Status.PAID);
      invoiceRepository.save(invoice);
      prepareTicketFromInvoice(invoice);
    } else {
      throw new ValidationException(
          "Нельзя присвоить статус PAID инвойсу в статусе " + invoice.getStatus());
    }
  }

  private void prepareTicketFromInvoice(Invoice invoice) {
    TicketCreateDto ticketInInvoice = getTicketDataFromInvoice(invoice);
    Ticket ticket = Ticket.builder()
            .data(JsonMapper.mapNumbersToJson(ticketInInvoice.getNumbers()))
            .user(invoice.getUser())
            .draw(drawService.getDrawById(ticketInInvoice.getDrawId()))
            .build();
    ticketService.saveTicket(ticket);
  }

  public void setUnpaid(Long id) {
    Invoice invoice = getInvoice(id);
    // isInvoiceNotCancelled(invoice);
    // здесь не нужен, т.к. можно перевести в неоплаченные, если запрос на изменение статуса после
    // завершения тиража
    if (invoice.getStatus() == Invoice.Status.PENDING) {
      invoice.setStatus(Invoice.Status.UNPAID);
      invoiceRepository.save(invoice);
    }
  }

  // TODO: отменить все неоплаченные инвойсы после окончания тиража
  @Transactional
  public void cancelUnpaidInvoicesWhenDrawStopped(Long drawId) {

    List<Invoice.Status> statuses = Arrays.asList(Invoice.Status.UNPAID, Invoice.Status.PENDING);
    List<Invoice> unpaidInvoices = invoiceRepository.findAllByStatusInAndCancelled(statuses, 0);

    //    выбрать все неоплаченные инвойсы именно для указанного тиража
    List<Invoice> invoicesToCancel =
        unpaidInvoices.stream()
            .filter(
                invoice -> {
                  try {
                    // Парсим JSON и достаём drawId
                    TicketCreateDto dto =
                        JsonMapper.fromJson(invoice.getTicketData(), TicketCreateDto.class);
                    return dto.getDrawId().equals(drawId);
                  } catch (Exception e) {
                    log.error("Failed to parse ticketData: {}", invoice.getTicketData(), e);
                    return false;
                  }
                })
            .toList();

    invoicesToCancel.forEach(
        invoice -> {
          invoice.setCancelled(1);
          if (invoice.getStatus() == Invoice.Status.PENDING) {
            setUnpaid(invoice.getId());
          }
        });

    invoiceRepository.saveAll(invoicesToCancel);
  }

  public Invoice getInvoice(Long id, Long userId) {
    return invoiceRepository
        .findByIdAndUser_Id(id, userId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Инвойс с ID " + id + " не найден или не принадлежит пользователю"));
  }

  public Invoice getInvoice(Long id) {
    return invoiceRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Инвойс с ID " + id + " не найден"));
  }

  private void isInvoiceNotCancelled(Invoice invoice) {
    if (invoice.getCancelled() == 1) {
      throw new ValidationException("Инвойс отменен, действия с ним невозможны");
    }
  }

  private Draw getDrawById(Long id) {
    return drawService.getDrawById(id);
  }

  private Long getDrawIdFromInvoice(Invoice invoice) {
    return getTicketDataFromInvoice(invoice).getDrawId();
  }

  private List<Integer> getTicketNumbersFromInvoice(Invoice invoice) {
    return getTicketDataFromInvoice(invoice).getNumbers();
  }

  private TicketCreateDto getTicketDataFromInvoice(Invoice invoice) {
    return JsonMapper.fromJson(invoice.getTicketData(), TicketCreateDto.class);
  }

  public void validateBuyingByInvoice(Invoice invoice) {
    Draw draw = getDrawById(getDrawIdFromInvoice(invoice));
    validator.validateForBuyingByDraw(draw);
    isInvoiceNotCancelled(invoice);
    if (invoice.getStatus() != Invoice.Status.UNPAID) {
      throw new ValidationException("Только неоплаченные инвойсы можно оплатить");
    }
  }
}
