package com.example.lottery.repository;

import com.example.lottery.entity.Draw;
import com.example.lottery.entity.PreGeneratedTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreGeneratedTicketRepository extends JpaRepository<PreGeneratedTicket, Long> {
    Optional<PreGeneratedTicket> findFirstByDrawAndIssuedFalse(Draw draw);

    void deleteByDraw(Draw draw);

    boolean existsByDrawAndNumbers(Draw draw, String s);
}
