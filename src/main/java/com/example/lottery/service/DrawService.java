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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class DrawService {
    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private DrawResultRepository drawResultRepository;

    @Autowired
    private LotteryTypeRepository lotteryTypeRepository;

    private final DrawMapper drawMapper;

    @Value("${app.default.max-po0l-size}")
    private Integer maxPoolSize;

    private ScheduledExecutorService executorPlanned = null;
    private ScheduledExecutorService executorActive = null;

    public DrawService(DrawMapper drawMapper) {
        this.drawMapper = drawMapper;
    }

    @PostConstruct
    public void init() {
        System.out.println("Init DrawService bgn");

        //int poolMaxSize = (int)lotteryTypeRepository.count();
        //System.out.format("Max size of lottery types: %d\n", poolMaxSize);

        executorActive = Executors.newScheduledThreadPool(maxPoolSize);
        executorPlanned = Executors.newScheduledThreadPool(maxPoolSize);
        checkActiveDraws();
        checkPlannedDraws();

        System.out.println("Init DrawService end");
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

    public void setStatusComplete(Draw draw) {
        draw.setStatus(DrawStatus.COMPLETED);
        drawRepository.save(draw);
    }

    public boolean existsSameLotteryOnDay(Long lotteryTypeId, LocalDateTime startTime) {
        LocalDate date = startTime.toLocalDate();
        LocalDateTime startTimeAtMidnight = date.atStartOfDay();
        LocalDateTime endOfDay = startTimeAtMidnight.plusDays(1).minusNanos(1);

        return drawRepository.existsByLotteryType_IdAndStartTimeBetween(lotteryTypeId, startTimeAtMidnight, endOfDay);
    }

    private void checkActiveDraws(){
        // Получаем завтрашнюю дату
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

        List<Draw> draws = drawRepository.findByStatusAndStartTimeBeforeTimeOrdered(DrawStatus.ACTIVE, tomorrow);
        System.out.format("Get Active draws: %d\n", draws.size());
        for (var draw : draws) {
            var now = LocalDateTime.now();
            var drawStartTime = draw.getStartTime();
            var drawDuration = draw.getDuration();

            System.out.format("id: %d name: %s status: %s\n", draw.getId(), draw.getName(), draw.getStatus());

            /*if (drawStartTime.plusMinutes(drawDuration).isBefore(now)) {
                System.out.println("Set complete");
                setStatusComplete(draw);
                continue;
            }*/

           // if ()
        }
    }


    private void checkPlannedDraws(){

    }
}
