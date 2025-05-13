package com.example.lottery.service;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.lottery.dto.PaymentCreateDto;
import com.example.lottery.dto.PaymentDto;
import com.example.lottery.dto.TicketInInvoiceDto;
import com.example.lottery.entity.Invoice;
import com.example.lottery.entity.Payment;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.exception.ValidationException;
import com.example.lottery.mapper.PaymentMapper;
import com.example.lottery.mock.MockPaymentProcessor;
import com.example.lottery.repository.PaymentRepository;
import com.example.lottery.service.utils.PaymentLinkGenerator;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
  private final PaymentRepository paymentRepository;
  private final MockPaymentProcessor mockPaymentProcessor;
  private final InvoiceService invoiceService;
  private final PaymentMapper paymentMapper;
  private final PaymentLinkGenerator paymentLinkGenerator;
  private final UserService userService;

  @Transactional
  public String process(PaymentCreateDto dto, Long userId) {
    Long invoiceId = dto.getInvoiceId();
    Invoice invoice = invoiceService.getInvoice(invoiceId);
    String cardNumber = dto.getCardNumber();
    String cvc = dto.getCvc();
    TicketInInvoiceDto ticketInInvoice = null;
    try {
      ticketInInvoice = paymentLinkGenerator.decodePaymentLink(invoice.getPaymentLink());
    } catch (Exception e) {
      throw new ValidationException("Не удалось получить данные из платежной ссылки");
    }
    BigDecimal amount = ticketInInvoice.getTicketPrice();
    log.debug("DrawID from payment link: {}", ticketInInvoice.getDrawId());

    // Проверка: если инвойс оплачен, отменен, либо его тираж не активен — запретить
    invoiceService.validateBuyingByInvoice(invoice);

    // в пендинг до оплаты
    invoiceService.setPending(invoiceId, userId);

    // Обработка платежа
    Payment.Status paymentResponseStatus = mockPaymentProcessor.process(cardNumber, cvc);

    // Формируем сущность платежа для хранения в БД
    Payment payment =
        Payment.builder().invoice(invoice).amount(amount).status(paymentResponseStatus).build();
    Payment savedPayment = paymentRepository.save(payment);

    // Завершить процесс
    if (paymentResponseStatus == Payment.Status.SUCCESS) {
      invoiceService.setPaid(invoiceId);
      return "Оплата прошла успешно";
    } else {
      invoiceService.setUnpaid(invoiceId);
      return "К сожалению, оплата не прошла";
    }
  }

  public Optional<PaymentDto> getPaymentById(Long id) {
    return paymentRepository.findById(id).map(paymentMapper::toDto);
  }

  public PaymentDto getPaymentByIdAndUser(Long id, Long userId) {
    return paymentRepository.findById(id)
            .filter(payment -> payment.getInvoice().getUser().getId().equals(userId))
            .map(paymentMapper::toDto)
            .orElseThrow(() -> paymentRepository.existsById(id)
                    ? new AccessDeniedException("Вы не можете просматривать этот платёж")
                    : new NotFoundException("Платёж не найден"));
  }
}
