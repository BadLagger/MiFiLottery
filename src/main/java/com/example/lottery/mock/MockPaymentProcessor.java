package com.example.lottery.mock;

import com.example.lottery.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MockPaymentProcessor {
    private final Random random = new Random();

    public Payment.Status process(String cardNumber, String cvc) {
        if (!"123".equals(cvc)) {
            return Payment.Status.FAILED;
        }
        // 80% шанс успешной оплаты при cvc=123
        return random.nextInt(100) < 80 ? Payment.Status.SUCCESS : Payment.Status.FAILED;
    }
}