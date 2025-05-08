package com.example.lottery.repository;

import com.example.lottery.entity.Ticket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
  List<Ticket> findByUserId(Long userId);

  List<Ticket> findAllTicketsByDrawId(Long drawId);
}
