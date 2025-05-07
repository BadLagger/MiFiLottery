package com.example.lottery.repository;

import com.example.lottery.entity.Invoice;
import com.example.lottery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatusAndUser_Id(Invoice.Status status, Long userId);

    Optional<Invoice> findByIdAndUser_Id(Long id, Long userId);

    List<Invoice> findByStatus(Invoice.Status status);
}