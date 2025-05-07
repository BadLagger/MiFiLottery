package com.example.lottery.repository;

import com.example.lottery.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);

    //запрос UNPAID инвойсов определенного тиража (ticket.drawId)
    @Query(value = "SELECT * FROM invoice i " +
            "WHERE i.status = 'UNPAID' " +
            "AND (i.ticketdata->>'drawId')::int = :drawId", nativeQuery = true)
    List<Invoice> findUnpaidInvoicesByDrawId(@Param("drawId") Long drawId);

}
