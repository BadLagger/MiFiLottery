package com.example.lottery.repository;

import com.example.lottery.entity.Invoice;
import com.example.lottery.dto.InvoiceStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InvoiceRepository {
    private final Map<Long, Invoice> storage = new ConcurrentHashMap<>();

    public Invoice save(Invoice invoice) {
        storage.put(invoice.getId(), invoice);
        return invoice;
    }

    public Optional<Invoice> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Invoice> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Invoice> findByStatus(InvoiceStatus status) {
        List<Invoice> result = new ArrayList<>();
        for (Invoice invoice : storage.values()) {
            if (invoice.getStatus() == status) result.add(invoice);
        }
        return result;
    }
}