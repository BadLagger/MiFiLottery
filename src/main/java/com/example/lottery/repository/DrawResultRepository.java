package com.example.lottery.repository;

import com.example.lottery.entity.Draw;
import com.example.lottery.entity.DrawResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawResultRepository extends JpaRepository<DrawResult, Long> {
    DrawResult findByDraw(Draw byId);
}
