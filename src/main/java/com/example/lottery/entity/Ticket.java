package com.example.lottery.entity;

import com.example.lottery.converter.JsonNodeConverter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "Ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter= JsonNodeConverter.class)
    @Column(columnDefinition="jsonb")
    private JsonNode data; // например {"numbers": [1, 5, 10, 15, 20]}

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "draw_id")
    private Draw drawId;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        INGAME,
        WIN,
        LOSE,
        CANCELLED
    }
}
