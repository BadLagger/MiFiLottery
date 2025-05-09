package com.example.lottery.entity;

import com.example.lottery.mapper.JsonMapper;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.CRC32;

@Entity
@Table(name = "pre_generated_tickets")
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

  @Column(name = "numbers_hash")
  private String numbersHash;

  @Column(nullable = false)
  private boolean issued = false; // Флаг "выдан"

  @PrePersist
  @PreUpdate
  public void calculateHash() {
    if (this.numbers != null) {
      List<Integer> nums = JsonMapper.mapJsonToNumbers(this.numbers);
      this.numbersHash = calculateCrc32(nums);
    }
  }

  private String calculateCrc32(List<Integer> numbers) {
    CRC32 crc32 = new CRC32();
    crc32.update(numbers.toString().getBytes(StandardCharsets.UTF_8));
    return Long.toHexString(crc32.getValue());
  }
}
