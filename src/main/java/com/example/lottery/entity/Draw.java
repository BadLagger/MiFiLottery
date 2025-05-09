package com.example.lottery.entity;

import com.example.lottery.dto.DrawStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "draw")
public class Draw {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "lottery_type_id")
  private LotteryType lotteryType;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private Integer duration;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "varchar(255) check (status in ('PLANNED','ACTIVE','COMPLETED','CANCELLED'))")
  private DrawStatus status;
}
