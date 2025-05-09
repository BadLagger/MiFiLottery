package com.example.lottery.service;

import com.example.lottery.dto.DrawRequestDto;
import com.example.lottery.dto.DrawStatus;
import com.example.lottery.dto.algorithm.AlgorithmRules;
import com.example.lottery.dto.algorithm.FixedPoolRules;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.DrawResult;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.exception.NotFoundException;
import com.example.lottery.entity.PreGeneratedTicket;
import com.example.lottery.mapper.DrawMapper;
import com.example.lottery.mapper.TicketMapper;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.DrawResultRepository;
import com.example.lottery.repository.LotteryTypeRepository;
import com.example.lottery.repository.PreGeneratedTicketRepository;
import com.example.lottery.service.Impl.FixedPoolTicketGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class DrawService {
  private final DrawRepository drawRepository;

  private final DrawResultRepository drawResultRepository;

    private final DrawResultService drawResultService;
  private final LotteryTypeRepository lotteryTypeRepository;
  private final TicketPoolService ticketPoolService;
    private TicketMapper ticketMapper;
    private FixedPoolTicketGenerator fixedPoolTicketGenerator;
    private PreGeneratedTicketRepository preGeneratedRepo;
    private TicketsFactory ticketsFactory;

  private final DrawMapper drawMapper;

  @Value("${app.default.max-pool-size}")
  private Integer maxPoolSize;

  private ScheduledExecutorService executorPlanned = null;
  private ScheduledExecutorService executorActive = null;

  private ConcurrentHashMap<Long, ScheduledFuture<?>> sсheduledActiveFutures = null;
  private ConcurrentHashMap<Long, ScheduledFuture<?>> sсheduledPlannedFutures = null;

  @PostConstruct
  public void init() {
       // System.out.println("Init DrawService bgn");

    // Пока что максимальное количество потоков прибито гвоздями, но по хорошему надо сделать его
    // гибким - maxPoolSize = maxLotteryTypes * maxNumberOfLOtteryTypesInDay (то есть максимальное
    // кол-во потоков равно количеству типов лотерей плюс максимальное кол-во лотереи каждого типа в
    // сутки)
    // Возможно рациональней использовать ProjectLoom ?
    executorActive = Executors.newScheduledThreadPool(maxPoolSize);
    executorPlanned = Executors.newScheduledThreadPool(maxPoolSize);
    sсheduledActiveFutures = new ConcurrentHashMap<>();
    sсheduledPlannedFutures = new ConcurrentHashMap<>();
    checkActiveDraws();
    checkPlannedDraws();

      //  System.out.println("Init DrawService end");
  }

  public Draw getDrawById(Long id) {
    return drawRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Тираж с ID = " + id + " не найден"));
  }

  public BigDecimal getTicketPriceByDrawId(Long id) {
    Draw draw = getDrawById(id);
    return draw.getLotteryType().getTicketPrice();
  }

  public LotteryType getLotteryTypeByDrawId(Long drawId) {
    return getDrawById(drawId).getLotteryType();
  }

  public List<Draw> findAll() {
    return drawRepository.findAll();
  }

  public List<Draw> findByStatus(DrawStatus status) {
    return drawRepository.findByStatus(status.toString());
  }

  public Optional<Draw> findById(Long id) {
    return drawRepository.findById(id);
  }

  public Draw save(Draw draw) {
    return drawRepository.save(draw);
  }

  public Draw createDraw(DrawRequestDto request) {
    LotteryType lotteryType =
        lotteryTypeRepository
            .findById(request.lotteryTypeId())
            .orElseThrow(() -> new IllegalArgumentException("Type of lottery not found"));
    Draw draw = drawMapper.toEntity(request, lotteryType, DrawStatus.PLANNED);
    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
    // Если Тираж запланирован на сегодня, то добавляем его в планировщик
    if (draw.getStartTime().isBefore(tomorrow)) setPlanned(draw);
    else {
      // Если запланирован не на сегодня, то сохраняем только в БД
      setStatus(draw, DrawStatus.PLANNED);
    }
    return draw;
  }

  public void setStatus(Draw draw, DrawStatus status) {
    draw.setStatus(status);
    drawRepository.save(draw);
  }

  public void setActive(Draw draw) {
       // System.out.format("Draw: %s set active\n", draw.getName());
    // Устанавливаем в планировщик активных задач
    setStatus(draw, DrawStatus.ACTIVE);
    long delayMs =
        ChronoUnit.MILLIS.between(
            LocalDateTime.now(), draw.getStartTime().plusMinutes(draw.getDuration()));
    addToActiveTasks(draw, delayMs);
    // Удаляем из списка планировщика запланированных задач
    // TODO: Если тираж с предсозданными билетами - запустить initPoolForDraw
    sсheduledPlannedFutures.remove(draw.getId());
  }

  public void setPlanned(Draw draw) {
     //   System.out.format("Draw: %s set planned\n", draw.getName());
    setStatus(draw, DrawStatus.PLANNED);
    long delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), draw.getStartTime());
    addToPlannedTasks(draw, delayMs);
  }

  public void setComplete(Draw draw) {
//    System.out.format("Draw: %s set complete\n", draw.getName());
    //        preGeneratedTicketRepo.deleteByDraw(draw);
      // TODO: Очищаем пул предсозданных билетов
    setStatus(draw, DrawStatus.COMPLETED);
    sсheduledActiveFutures.remove(draw.getId());
        // Пробуем сгенерировать результат
        drawResultService.generateResult(draw.getId());
  }

  public void setCancel(Draw draw) {
    // Проверяем список активных задач
    var future = sсheduledActiveFutures.remove(draw.getId());
    if (future != null) {
      future.cancel(false);
    } else {
      // Проверяем список запланированных задач
      future = sсheduledPlannedFutures.remove(draw.getId());
      if (future != null) {
        future.cancel(false);
      }
    }
      //  System.out.format("Draw: %s set cancelled\n", draw.getName());
    setStatus(draw, DrawStatus.CANCELLED);
  }

  public void addToActiveTasks(Draw draw, long delayMs) {
    var future = executorActive.schedule(() -> setComplete(draw), delayMs, TimeUnit.MILLISECONDS);
    sсheduledActiveFutures.put(draw.getId(), future);
  }

  public void addToPlannedTasks(Draw draw, long delayMs) {
    var future = executorPlanned.schedule(() -> setActive(draw), delayMs, TimeUnit.MILLISECONDS);
    sсheduledPlannedFutures.put(draw.getId(), future);
  }

  public boolean existsSameLotteryOnDay(Long lotteryTypeId, LocalDateTime startTime) {
    LocalDate date = startTime.toLocalDate();
    LocalDateTime startTimeAtMidnight = date.atStartOfDay();
    LocalDateTime endOfDay = startTimeAtMidnight.plusDays(1).minusNanos(1);

    // Проверка на тип лотереи в течении этих суток без учёта статуса (Первоначальная реализация)
    // return drawRepository.existsByLotteryType_IdAndStartTimeBetween(lotteryTypeId,
    // startTimeAtMidnight, endOfDay);

    // Проверка(отладка) списка тиражей со статусами
    /*var draws = drawRepository.findByLotteryType_IdAndStartTimeBetweenAndStatusIn(lotteryTypeId,
            startTimeAtMidnight,
            endOfDay,
            Arrays.asList(DrawStatus.ACTIVE, DrawStatus.PLANNED));

    System.out.format("Dublicated draws: %d\n", draws.size());
    for (var draw : draws) {
        System.out.format("name: %s status: %s\n", draw.getName(), draw.getStatus());
    }*/

    // Проверка на тип лотереи в течении этих суток с учётом статусов ACTIVE и PLANNED
    return drawRepository.existsByLotteryType_IdAndStartTimeBetweenAndStatusIn(
        lotteryTypeId,
        startTimeAtMidnight,
        endOfDay,
        Arrays.asList(DrawStatus.ACTIVE, DrawStatus.PLANNED));
  }

  // Запускаемся в начале каждых суток и устанавливаем в планировщик задачи
  @Scheduled(cron = "0 0 0 * * ?")
  public void dailyDrawChecking() {
      //  System.out.println("Daily checking for draws status");
    checkActiveDraws();
    checkPlannedDraws();
  }

  private void checkActiveDraws() {
    // Получаем дату равную началу следующих суток
    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);
    // Получаем все записи из БД с тиражами, которые имеют активный статус и имеют до начала
    // следующих суток, при этом все записи упорядочены по дате от самых старых до самых свежих
    List<Draw> draws =
        drawRepository.findByStatusAndStartTimeBeforeTimeOrdered(DrawStatus.ACTIVE, tomorrow);
//    System.out.format("Get Active draws: %d\n", draws.size());
    // Проверка каждой записи
    for (var draw : draws) {
      // получаем время окончания тиража
      var drawEndTime = draw.getStartTime().plusMinutes(draw.getDuration());
    // Если время окончания тиража уже вышло, то завершаем тираж
        //    System.out.format("id: %d name: %s status: %s\n", draw.getId(), draw.getName(), draw.getStatus());
      if (drawEndTime.isBefore(LocalDateTime.now())) {
        setComplete(draw);
      } else {
        // Если тираж ещё актуален, то добавляем его в планировщик
           //     System.out.println("Add to active tasks");
        long delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), drawEndTime);
        addToActiveTasks(draw, delayMs);
      }
    }
  }

  private void checkPlannedDraws() {
    // Получаем дату равную началу следующих суток
    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS);

        // Получаем все записи из БД с тиражами, которые имеют запланированный статус и имеют до начала следующих суток, при этом все записи упорядочены по дате от самых старых до самых свежих
        List<Draw> draws = drawRepository.findByStatusAndStartTimeBeforeTimeOrdered(DrawStatus.PLANNED, tomorrow);
   //     System.out.format("Get Planned draws: %d\n", draws.size());
    for (var draw : draws) {
      var drawStartTime = draw.getStartTime();
      //      System.out.format("id: %d name: %s status: %s\n", draw.getId(), draw.getName(), draw.getStatus());
      // Если время окончания тиража прошло, то отменяем тираж
      if (drawStartTime.plusMinutes(draw.getDuration()).isBefore(LocalDateTime.now())) {
        setCancel(draw);
        continue;
      }
      // Если начало тиража уже прошло, то заносим тираж в планировщик активных тиражей
      if (drawStartTime.isBefore(LocalDateTime.now())) {
        //        System.out.println("Add to active tasks");
                long delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), drawStartTime.plusMinutes(draw.getDuration()));
        addToActiveTasks(draw, delayMs);
        continue;
      }
      // В остальных же случаях добавляем в планировщих запланированных тиражей
      long delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), drawStartTime);
      addToPlannedTasks(draw, delayMs);
    }
  }

  // TODO: Вызываем этот метод при создании тиража (после проверки на тип тиража).
  public void initPoolForDraw(Draw draw) {
    ticketPoolService.generateTicketsPoolForDraw(draw);
  }
}
