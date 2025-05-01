package com.example.lottery.repository;

import com.example.lottery.entity.Draw;
import com.example.lottery.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findById(Long id);

    List<Ticket> findByDraw(Draw draw);
//    List<Ticket> findByUserId(Long userId);
//    List<Ticket> findByDrawId(Long drawId);
}