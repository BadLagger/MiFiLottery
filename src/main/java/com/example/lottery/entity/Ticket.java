package com.example.lottery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "Ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "jsonb", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private String data; // например {"numbers": [1, 5, 10, 15, 20]}

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "draw_id", nullable = false)
  private Draw draw;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @PrePersist
  protected void onCreate() {
    if (status == null) {
      status = Status.INGAME;
    }
  }

  public enum Status {
    INGAME,
    WIN,
    LOSE,
    CANCELLED
  }
}
