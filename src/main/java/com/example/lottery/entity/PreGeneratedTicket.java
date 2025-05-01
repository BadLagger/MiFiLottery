package com.example.lottery.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PREGENERATEDTICKETS")
@Data
public class PreGeneratedTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw; // Связь с тиражом

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String numbers; // JSON с числами, например "[1, 5, 10]"

    @Column(nullable = false)
    private boolean issued = false; // Флаг "выдан"
}
