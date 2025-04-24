package com.mifisf.lottery.app.repository;

import com.mifisf.lottery.app.entity.Draw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrawRepository extends JpaRepository<Draw, Long> {
    @Query(value = "SELECT * FROM draw WHERE status::VARCHAR = :status", nativeQuery = true)
    List<Draw> findByStatus(@Param("status") String status);
}