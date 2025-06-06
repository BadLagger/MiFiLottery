package com.example.lottery.repository;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.entity.Draw;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface DrawRepository extends JpaRepository<Draw, Long> {
    @Query(value = "SELECT * FROM draw WHERE status::VARCHAR = :status", nativeQuery = true)
    List<Draw> findByStatus(@Param("status") String status);

    boolean existsByLotteryType_IdAndStartTimeBetween(Long lotteryTypeId, LocalDateTime startTimeAtMidnight, LocalDateTime endOfDay);

    boolean existsByLotteryType_IdAndStartTimeBetweenAndStatusIn(Long lotteryTypeId, LocalDateTime startTime, LocalDateTime endTime, Collection<DrawStatus> excludedStatuses);

    List<Draw> findByLotteryType_IdAndStartTimeBetweenAndStatusIn(Long lotteryTypeId, LocalDateTime startTime, LocalDateTime endTime, Collection<DrawStatus> excludedStatuses);

    @Query("SELECT d FROM Draw d WHERE d.status = :status AND d.startTime < :time ORDER BY d.startTime ASC")
    List<Draw> findByStatusAndStartTimeBeforeTimeOrdered(@Param("status") DrawStatus status, @Param("time") LocalDateTime time);
}