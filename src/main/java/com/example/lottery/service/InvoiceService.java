package com.example.lottery.service;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketInInvoiceDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.mapper.InvoiceMapper;
import com.example.lottery.mapper.JsonMapper;
import com.example.lottery.repository.InvoiceRepository;
import com.example.lottery.service.utils.PaymentLinkGenerator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceService {
  private final InvoiceRepository invoiceRepository;
  private final InvoiceMapper invoiceMapper;
  private final TicketService ticketService;
  private final DrawService drawService;
  private final PaymentLinkGenerator paymentLinkGenerator;

  public TicketInInvoiceDto createInvoice(TicketCreateDto dto, Long userId) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime now = LocalDateTime.now();

    InvoiceDto invoice = new InvoiceDto();
    invoice.setUserId(userId);
    invoice.setRegisterTime(now);
    invoice.setTicketData(JsonMapper.toJson(dto));
    invoice.setStatus(Invoice.Status.UNPAID);

    TicketInInvoiceDto ticketInInvoice = new TicketInInvoiceDto();
    ticketInInvoice.setUserId(userId);
    ticketInInvoice.setDrawId(dto.getDrawId());
    ticketInInvoice.setRegisterTime(now.format(formatter));
    ticketInInvoice.setNumbers(dto.getNumbers());
    ticketInInvoice.setTicketPrice(drawService.getTicketPriceByDrawId(dto.getDrawId()));
    ticketInInvoice.setPaymentLink(paymentLinkGenerator.generatePaymentLink(ticketInInvoice));

    invoice.setPaymentLink(ticketInInvoice.getPaymentLink());

    Invoice savedInvoice = invoiceRepository.save(invoiceMapper.toEntity(invoice));
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

  public void setPaid(Long id) {
    Invoice invoice = getInvoice(id);

    isInvoiceNotCancelled(invoice);
    if (invoice.getStatus() == Invoice.Status.PENDING) {
      invoice.setStatus(Invoice.Status.PAID);
      invoiceRepository.save(invoice);
    } else {
      throw new ValidationException(
          "Нельзя присвоить статус PAID инвойсу в статусе " + invoice.getStatus());
    }
  }

  public void setUnpaid(Long id) {
    Invoice invoice = getInvoice(id);
    // isInvoiceNotCancelled(invoice);
    // здесь не нужен, т.к. можно перевести в неоплаченные, если запрос на изменение статуса после завершения тиража
    if (invoice.getStatus() == Invoice.Status.PENDING) {
      invoice.setStatus(Invoice.Status.UNPAID);
      invoiceRepository.save(invoice);
    }
  }

  @Transactional
  public void cancelUnpaidInvoicesAfterDraw(Long drawId) {
    List<Invoice.Status> statuses = Arrays.asList(Invoice.Status.UNPAID, Invoice.Status.PENDING);
    List<Invoice> unpaidInvoices = invoiceRepository.findAllUnpaidInvoicesByDrawId(drawId, statuses);

    unpaidInvoices.forEach(invoice -> {
      invoice.setCancelled(1);
      if (invoice.getStatus() == Invoice.Status.PENDING) {
        setUnpaid(invoice.getId());
      }
    });

    invoiceRepository.saveAll(unpaidInvoices);
  }

  private Invoice getInvoice(Long id, Long userId) {
    return invoiceRepository
        .findByIdAndUser_Id(id, userId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Инвойс с ID " + id + " не найден или не принадлежит пользователю"));
  }

  private Invoice getInvoice(Long id) {
    return invoiceRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Инвойс с ID " + id + " не найден"));
  }

  private void isInvoiceNotCancelled(Invoice invoice) {
    if (invoice.getCancelled() == 1) {
      throw new ValidationException("Инвойс отменен, действия с ним невозможны");
    }
  }
}
