package com.example.lottery.entity;

import com.example.lottery.dto.InvoiceStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @OneToOne
    @JoinColumn(name = "ticket_data")
    private JsonNode ticketData;  // например {"numbers": [1, 5, 10, 15, 20]}

    private LocalDateTime registerTime;

    private String paymentLink;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private int cancelled; // 0 - не отменен, 1 - отменен  по окончании тиража все неоплаченные инвойсы становятся cancelled
}