package com.mifisf.lottery.app.repository;

import com.mifisf.lottery.app.entity.LotteryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotteryRepository extends JpaRepository<LotteryEntity, Long> {
}
