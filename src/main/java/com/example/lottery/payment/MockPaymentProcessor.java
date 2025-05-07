package com.example.lottery.payment;

import com.example.lottery.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MockPaymentProcessor {
    private final Random random = new Random();

    public Payment.PaymentStatus process(String cardNumber, String cvc) {
        if (!"123".equals(cvc)) {
            return Payment.PaymentStatus.FAILED;
        }
        // 80% шанс успешной оплаты при cvc=123
        return random.nextInt(100) < 80 ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED;
    }
}
