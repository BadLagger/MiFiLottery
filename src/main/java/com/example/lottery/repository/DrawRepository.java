package com.example.lottery.repository;

import com.example.lottery.entity.Draw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
//todo вернуть аннотацию, когда будет бд
public interface DrawRepository extends JpaRepository<Draw, Long> {
    List<Draw> findByStatus(Draw.Status status);
}