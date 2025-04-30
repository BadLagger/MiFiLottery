package com.example.lottery.payment;

import com.example.lottery.dto.DrawResultDto;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class MockPaymentProcessor {
    private final Random random = new Random();

    public DrawResultDto.PaymentStatus process(String cardNumber, String cvc) {
        if (!"123".equals(cvc)) {
            return DrawResultDto.PaymentStatus.FAILED;
        }
        // 80% шанс успеха при cvc=123
        return random.nextInt(100) < 80 ? DrawResultDto.PaymentStatus.SUCCESS : DrawResultDto.PaymentStatus.FAILED;
    }
}