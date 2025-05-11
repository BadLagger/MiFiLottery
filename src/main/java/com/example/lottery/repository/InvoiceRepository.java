package com.example.lottery.repository;

import com.example.lottery.entity.Invoice;
import com.example.lottery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatusAndUser_Id(Invoice.Status status, Long userId);

    Optional<Invoice> findByIdAndUser_Id(Long id, Long userId);

    List<Invoice> findByStatus(Invoice.Status status);

//    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId AND i.status IN (:statuses) AND i.cancelled = 0")
//    List<Invoice> getAllUnpaidInvoicesByUserId(
//            @Param("userId") Long userId,
//            @Param("statuses") List<Invoice.Status> statuses
//    );

//    @Query(
//            value = """
//        UPDATE invoice
//        SET cancelled = 1,
//            status = CASE WHEN status = 'PENDING' THEN 'UNPAID' ELSE status END
//        WHERE status IN ('UNPAID', 'PENDING')
//        AND cancelled = 0
//        AND ticket_data::jsonb->>'drawId' = :drawId
//        """,
//            nativeQuery = true
//    )
//    void cancelUnpaidInvoicesByDrawId(@Param("drawId") String drawId);

    List<Invoice> findAllByStatusInAndCancelled(Collection<Invoice.Status> statuses, int cancelled);
}