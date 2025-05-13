package com.example.lottery.repository;

import com.example.lottery.entity.LotteryType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotteryTypeRepository extends JpaRepository<LotteryType, Long> {
    boolean existsByDescription(@NotBlank(message = "Описание обязательно") String description);
}
