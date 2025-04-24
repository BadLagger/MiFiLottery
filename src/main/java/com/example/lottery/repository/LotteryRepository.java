package com.example.lottery.repository;

import com.example.lottery.entity.LotteryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotteryRepository extends JpaRepository<LotteryType, Long> {
}
