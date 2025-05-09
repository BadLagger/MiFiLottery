package com.example.lottery.service.utils;

import com.example.lottery.dto.TicketInInvoiceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentLinkGenerator {

  @Value("${app.secret_key}")
  private String SECRET_KEY; // 16, 24 или 32 байта

  @Value("${app.payment_base_url}")
  private String PAYMENT_URL_BASE;

  public String generatePaymentLink(TicketInInvoiceDto invoice) {
    ObjectMapper mapper = new ObjectMapper();
    String jsonData = null;
    try {
      jsonData = mapper.writeValueAsString(invoice);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    String signature = hmacSHA256(jsonData, SECRET_KEY);
    String encodedData =
        Base64.getUrlEncoder().encodeToString(jsonData.getBytes(StandardCharsets.UTF_8));
    return String.format("%s?data=%s&signature=%s", PAYMENT_URL_BASE, encodedData, signature);
  }

  private String hmacSHA256(String data, String secret) {
    Mac sha256_HMAC = null;
    try {
      sha256_HMAC = Mac.getInstance("HmacSHA256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    SecretKeySpec secretKey =
        new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    try {
      sha256_HMAC.init(secretKey);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }
    byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return Base64.getUrlEncoder().encodeToString(hash);
  }

  public boolean verifyPaymentRequest(String encodedData, String signature, String secretKey) {
    try {
      String decodedData = new String(Base64.getUrlDecoder().decode(encodedData));
      String expectedSignature = hmacSHA256(decodedData, secretKey);
      return expectedSignature.equals(signature);
    } catch (Exception e) {
      return false;
    }
  }

  public TicketInInvoiceDto decodePaymentLink(String paymentUrl) throws Exception {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(paymentUrl);
    String encodedData = builder.build().getQueryParams().getFirst("data");
    String signature = builder.build().getQueryParams().getFirst("signature");

    if (!verifyPaymentRequest(encodedData, signature, SECRET_KEY)) {
      //            throw new SecurityException("Подпись недействительна!");
      log.error("Payment link verification failed");
    }

    return decodeData(encodedData);
  }

  private TicketInInvoiceDto decodeData(String encodedData) throws Exception {
    // Декодируем из Base64
    byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedData);
    String jsonData = new String(decodedBytes, StandardCharsets.UTF_8);

    // Преобразуем JSON в объект
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(jsonData, TicketInInvoiceDto.class);
  }
}
