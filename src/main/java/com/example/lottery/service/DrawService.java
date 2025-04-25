package com.example.lottery.service;

import com.example.lottery.dto.DrawRequestDto;
import com.example.lottery.dto.DrawResultDto;
import com.example.lottery.dto.DrawStatus;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.DrawResult;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.mapper.DrawMapper;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.DrawResultRepository;
import com.example.lottery.repository.LotteryTypeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DrawService {
    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private DrawResultRepository drawResultRepository;

    @Autowired
    private LotteryTypeRepository lotteryTypeRepository;

    private final DrawMapper drawMapper;

    public DrawService(DrawMapper drawMapper) {
        this.drawMapper = drawMapper;
    }

    public List<Draw> findAll() {return drawRepository.findAll();}

    public List<Draw> findByStatus(DrawStatus status) {
        return drawRepository.findByStatus(status.toString());
    }

    public Optional<Draw> findById(Long id) { return drawRepository.findById(id); }

    public DrawResult findResultByDrawId(Long id) {
        return  drawResultRepository.findByDraw(drawRepository.findById(id).orElse(null));
    }

    public Draw save(Draw draw) {
        return drawRepository.save(draw);
    }

    public Draw createDraw(DrawRequestDto request) {
        LotteryType lotteryType = lotteryTypeRepository.findById(request.lotteryTypeId()).orElseThrow(() -> new IllegalArgumentException("Type of lottery not found"));
        Draw draw = drawMapper.toEntity(request, lotteryType, DrawStatus.PLANNED);
        return drawRepository.save(draw);
    }

    public boolean existsSameLotteryOnDay(Long lotteryTypeId, LocalDateTime startTime) {
        LocalDate date = startTime.toLocalDate();
        LocalDateTime startTimeAtMidnight = date.atStartOfDay();
        LocalDateTime endOfDay = startTimeAtMidnight.plusDays(1).minusNanos(1);

        return drawRepository.existsByLotteryType_IdAndStartTimeBetween(lotteryTypeId, startTimeAtMidnight, endOfDay);
    }
}
