package com.example.lottery.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user")
    public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password; //
    @ManyToOne
    @JoinColumn(name = "role_id")
    @Setter(AccessLevel.PRIVATE)
    private Role role;
    private String telegram;
    private BigDecimal balance = BigDecimal.ZERO;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean isActive = true;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;


    private void setId(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Некорректный ID");
        }
        this.id = id;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username обязателен!");
        }
        this.username = username;
    }

    public void setPassword(String password) {

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password обязателен!");
        }
        if (password.length() < 5 || password.length() > 20) {
            throw new IllegalArgumentException("Неподходящая длина пароля");
        }
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());

    }

    public void assignRole(Role role) {
        if (role == null) throw new IllegalArgumentException("Роль не может быть пустой");
        this.role = role;

    }

    public void setTelegram(String telegram) {
        if (telegram == null) {
            throw new IllegalArgumentException("Telegram должен начинаться с @");
        }
        if (!telegram.startsWith("@") || telegram.length() < 5 || telegram.length() > 32) {
            throw new IllegalArgumentException("Телеграм должен начинаться с @ и иметь 5-32 символа");
        }
        this.telegram = telegram;
    }

    public void setBalance(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Баланс не может быть null");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }
        this.balance = balance;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}






