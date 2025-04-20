package com.mifisf.lottery.app.repositories;

import com.mifisf.lottery.app.models.LotteryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotteryRepository extends JpaRepository<LotteryEntity, Long> {
}
