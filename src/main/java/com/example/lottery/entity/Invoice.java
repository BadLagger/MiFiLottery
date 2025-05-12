package com.example.lottery.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

//  @Column(columnDefinition = "varchar") // Для H2
//  @JdbcTypeCode(SqlTypes.VARCHAR)     // Важно для корректной работы
  @Column(columnDefinition = "jsonb") // TODO: Для Postgres
  @JdbcTypeCode(SqlTypes.JSON)
  private String ticketData;

  @Column(nullable = false)
  private LocalDateTime registerTime;

  private String paymentLink;

  @Enumerated(EnumType.STRING)
  private Status status;

  private int cancelled = 0; // 0 - не отменен
  // 1 - отменен по окончании тиража все неоплаченные инвойсы становятся cancelled

  @PrePersist
  protected void onCreate() {
    if (registerTime == null) {
      registerTime = LocalDateTime.now();
    }
  }

  public enum Status {
    UNPAID, // инвойс создан, но не оплачен - по умолчанию
    PENDING, // ожидает оплаты, в этом статусе нельзя отменить (блокировка на оплату)
    PAID, // инвойс оплачен
    REFUNDED // средства возвращены
  }
}
