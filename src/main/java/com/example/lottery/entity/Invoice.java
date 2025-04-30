package com.example.lottery.entity;

import com.example.lottery.dto.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Long userId;

    private List<String> ticketData; //JSON каждого билета как String
cd
    private LocalDateTime registerTime;

    private String paymentLink;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private int cancelled; // 0 - не отменен, 1 - отменен   по окончании тиража все неоплаченные инвойсы становятся cancelled
}