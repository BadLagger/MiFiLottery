package com.example.lottery.service;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.TicketCreateDto;
import com.example.lottery.dto.TicketInInvoiceDto;
import com.example.lottery.dto.TicketResponseDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.entity.User;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.mapper.InvoiceMapper;
import com.example.lottery.mapper.JsonMapper;
import com.example.lottery.repository.InvoiceRepository;
import com.example.lottery.service.utils.PaymentLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {
  private final InvoiceRepository invoiceRepository;
  private final InvoiceMapper invoiceMapper;
  private final TicketService ticketService;
  private final DrawService drawService;
  private final PaymentLinkGenerator paymentLinkGenerator;

  public TicketInInvoiceDto createInvoice(TicketCreateDto dto) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    InvoiceDto invoice = new InvoiceDto();
    invoice.setUserId(1L);
    invoice.setRegisterTime(LocalDateTime.now());
    invoice.setTicketData(JsonMapper.toJson(dto));
    invoice.setStatus(Invoice.Status.UNPAID);

    TicketInInvoiceDto ticketInInvoice = new TicketInInvoiceDto();
    ticketInInvoice.setUserId(1L);
    ticketInInvoice.setDrawId(dto.getDrawId());
    ticketInInvoice.setRegisterTime(invoice.getRegisterTime().format(formatter));
    ticketInInvoice.setNumbers(dto.getNumbers());
    ticketInInvoice.setTicketPrice(drawService.getTicketPriceByDrawId(dto.getDrawId()));

    invoice.setPaymentLink(paymentLinkGenerator.generatePaymentLink(ticketInInvoice));
    ticketInInvoice.setPaymentLink(invoice.getPaymentLink());
    Invoice savedInvoice = invoiceRepository.save(invoiceMapper.toEntity(invoice));
    ticketInInvoice.setInvoiceId(savedInvoice.getId());
    return ticketInInvoice;
  }

  public List<InvoiceDto> getInvoicesByStatus(Invoice.Status status) {
    Long userId = 1L; // todo: getCurrentUser().getId();
    return invoiceRepository.findByStatusAndUser_Id(status, userId).stream()
        .map((invoiceMapper::toDto))
        .collect(Collectors.toList());
  }

  public InvoiceDto getInvoiceById(Long id) {
    Long userId = 1L; // todo: getCurrentUser().getId();
    return invoiceRepository
        .findByIdAndUser_Id(id, userId)
        .map(invoiceMapper::toDto)
        .orElseThrow(() -> new NotFoundException("Инвойс с ID " + id + " не найден"));
  }

  public InvoiceDto updateInvoiceStatus(Long id, Invoice.Status status, int cancelled) {
    Invoice invoice = invoiceRepository.findById(id).orElseThrow();
    invoice.setStatus(status);
    invoice.setCancelled(cancelled);
    return invoiceMapper.toDto(invoiceRepository.save(invoice));
  }

  public void setPending(Long invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
    if (invoice.getStatus() == Invoice.Status.UNPAID) {
      invoice.setStatus(Invoice.Status.PENDING);
      invoiceRepository.save(invoice);
    }
  }

  public void setPaid(Long invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
    if (invoice.getStatus() == Invoice.Status.PENDING) {
      invoice.setStatus(Invoice.Status.PAID);
      invoiceRepository.save(invoice);
    }
  }

  public void setUnpaid(Long invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
    if (invoice.getStatus() == Invoice.Status.PENDING) {
      invoice.setStatus(Invoice.Status.UNPAID);
      invoiceRepository.save(invoice);
    }
  }

  // toDo Отмена неоплаченных инвойсов после завершения тиража
  public void cancelUnpaidAfterDraw(Long drawId) {
    List<Invoice> unpaid = invoiceRepository.findByStatus(Invoice.Status.UNPAID);
    for (Invoice invoice : unpaid) {
      invoice.setCancelled(1);
      invoiceRepository.save(invoice);
    }
  }

  /*private InvoiceDto toDto(Invoice invoice) {
          return new InvoiceDto(
                  invoice.getId(),
                  invoice.getUser() == null ? null : invoice.getUser().getId(),
                  invoice.getTicketData(),
                  invoice.getRegisterTime(),
                  invoice.getPaymentLink(),
                  invoice.getStatus(),
                  invoice.getCancelled()
          );
      }
  */

}
