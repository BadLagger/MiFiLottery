package com.example.lottery.entity;

import com.example.lottery.dto.PaymentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime paymentTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        SUCCESS,
        FAILED
    }

    @PrePersist
    protected void onCreate() {
        if (paymentTime == null) {
            paymentTime = LocalDateTime.now();
        }
    }
}