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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

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

    private ConcurrentHashMap<Long, ScheduledFuture<?>> sheduledActiveFutures = null;

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
        sheduledActiveFutures = new ConcurrentHashMap<>();
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

    public void setStatus(Draw draw, DrawStatus status) {
        draw.setStatus(status);
        drawRepository.save(draw);
    }

    public void setComplete(Draw draw) {
        System.out.format("Draw: %s set complete\n", draw.getName());
        setStatus(draw, DrawStatus.COMPLETED);
        sheduledActiveFutures.remove(draw.getId());
    }

    public void addToActiveTasks(Draw draw, long delayMs) {
        var future = executorActive.schedule(()-> setComplete(draw), delayMs, TimeUnit.MILLISECONDS);
        sheduledActiveFutures.put(draw.getId(), future);
    }

    public void setCancel(Draw draw) {
        var future = sheduledActiveFutures.remove(draw.getId());
        if (future != null) {
            future.cancel(false);
        }
        System.out.format("Draw: %s set cancelled\n", draw.getName());
        setStatus(draw, DrawStatus.CANCELLED);
    }

    public boolean existsSameLotteryOnDay(Long lotteryTypeId, LocalDateTime startTime) {
        LocalDate date = startTime.toLocalDate();
        LocalDateTime startTimeAtMidnight = date.atStartOfDay();
        LocalDateTime endOfDay = startTimeAtMidnight.plusDays(1).minusNanos(1);

        return drawRepository.existsByLotteryType_IdAndStartTimeBetween(lotteryTypeId, startTimeAtMidnight, endOfDay);
    }

    private void checkActiveDraws(){
        // Получаем дату равную началу следующих суток
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);

        // Получаем все записи из БД с тиражами, которые имеют активный статус и имеют до начала следующих суток, при этом все записи упорядочены по дате от самых старых до самых свежих
        List<Draw> draws = drawRepository.findByStatusAndStartTimeBeforeTimeOrdered(DrawStatus.ACTIVE, tomorrow);
        System.out.format("Get Active draws: %d\n", draws.size());
        // Проверка каждой записи
        for (var draw : draws) {
            var nowTime = LocalDateTime.now();

            // получаем время окончания тиража
            var drawEndTime = draw.getStartTime().plusMinutes(draw.getDuration());

            System.out.format("id: %d name: %s status: %s\n", draw.getId(), draw.getName(), draw.getStatus());

            // Если время окончания тиража уже вышло, то завершаем тираж
            if (drawEndTime.isBefore(nowTime)) {
                setComplete(draw);
            } else {
                // Если тираж ещё актуален, то добавляем его в планировщик
                System.out.println("Add to active tasks");
                long delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), drawEndTime);
                addToActiveTasks(draw, delayMs);
            }
        }
    }

    private void checkPlannedDraws(){

    }
}
