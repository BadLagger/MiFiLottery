package com.example.lottery.entity;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON) //drawId, numbers берем отсюда
    private String ticketData; // например {"drawId: 123, "numbers": [1, 5, 10, 15, 20]}, хранение как JSON-строка

    @Column(nullable = false)
    private LocalDateTime registerTime;

    @Column(nullable = false)
    private String paymentLink; //toDO - нигде не используем

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Column(nullable = false)
    private int cancelled; // 0 - не отменен, 1 - отменен.

    public enum InvoiceStatus {
        UNPAID,    // инвойс создан, но не оплачен - по умолчанию
        PENDING,   // ожидает оплаты, в этом статусе нельзя отменить (блокировка на оплату)
        PAID,      // инвойс оплачен
        REFUNDED   // средства возвращены
    }
}
