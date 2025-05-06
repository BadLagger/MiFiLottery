package com.example.lottery.entity;

import com.example.lottery.dto.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String ticketData;

    private LocalDateTime registerTime;

    private String paymentLink;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNPAID;

    private int cancelled; // 0 - не отменен, 1 - отменен  по окончании тиража все неоплаченные инвойсы становятся cancelled

    public enum Status {
        UNPAID,    // инвойс создан, но не оплачен - по умолчанию
        PENDING,   // ожидает оплаты, в этом статусе нельзя отменить (блокировка на оплату)
        PAID,      // инвойс оплачен
        REFUNDED   // средства возвращены
    }
}