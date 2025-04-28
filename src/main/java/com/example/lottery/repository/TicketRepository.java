package com.example.lottery.repository;

import com.example.lottery.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
//    Ticket save(Ticket ticket);
//    Optional<Ticket> findById(Long id);
//    List<Ticket> findByDrawId(Long drawId);
}