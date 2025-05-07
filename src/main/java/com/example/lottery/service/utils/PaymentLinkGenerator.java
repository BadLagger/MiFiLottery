package com.example.lottery.service.utils;

import com.example.lottery.dto.InvoiceDto;
import com.example.lottery.dto.TicketInInvoiceDto;
import com.example.lottery.entity.Invoice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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

  public boolean verifyPaymentRequest(String encodedData, String signature, String secretKey)
      throws Exception {
    String decodedData = new String(Base64.getUrlDecoder().decode(encodedData));
    String expectedSignature = hmacSHA256(decodedData, secretKey);
    return expectedSignature.equals(signature);
  }
}
