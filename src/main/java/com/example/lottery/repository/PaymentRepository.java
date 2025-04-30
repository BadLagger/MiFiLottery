// PaymentRepository.java
package com.example.lottery.repository;

import com.example.lottery.entity.Payment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaymentRepository {
    private final Map<UUID, Payment> storage = new ConcurrentHashMap<>();

    public Payment save(Payment payment) {
        storage.put(payment.getId(), payment);
        return payment;
    }

    public Optional<Payment> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Payment> findAll() {
        return new ArrayList<>(storage.values());
    }
}