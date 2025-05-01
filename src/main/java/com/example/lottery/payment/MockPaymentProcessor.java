package com.example.lottery.payment;

import com.example.lottery.dto.PaymentStatus;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class MockPaymentProcessor {
    private final Random random = new Random();

    public PaymentStatus process(String cardNumber, String cvc) {
        if (!"123".equals(cvc)) {
            return PaymentStatus.FAILED;
        }
        // 80% шанс успешной оплаты при cvc=123
        return random.nextInt(100) < 80 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}
