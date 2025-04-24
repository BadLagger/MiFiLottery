package com.mifisf.lottery.app.repository;

import com.mifisf.lottery.app.entity.Draw;
import com.mifisf.lottery.app.entity.DrawResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrawResultRepository extends JpaRepository<DrawResult, Long> {
    DrawResult findByDraw(Draw byId);
}
