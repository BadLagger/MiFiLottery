package com.example.lottery.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String telegram;

  private BigDecimal balance;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "role_id")
  private Role role;
}
