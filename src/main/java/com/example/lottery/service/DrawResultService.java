package com.example.lottery.service;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.DrawResult;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.entity.Ticket;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.DrawResultRepository;
import com.example.lottery.repository.TicketRepository;
import com.example.lottery.service.notificationService.TelegramNotificationService;
import com.example.lottery.strategy.LotteryCheckStrategy;
import com.example.lottery.strategy.LotteryCheckStrategyResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawResultService {

    private final DrawRepository drawRepository;
    private final DrawResultRepository drawResultRepository;
    private final TicketRepository ticketRepository;
    private final LotteryCheckStrategyResolver strategyResolver;
    private final TelegramNotificationService telegramNotificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecureRandom random = new SecureRandom();

    public DrawResult generateResult(Long drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new RuntimeException("Draw not found"));

        if (draw.getStatus() != DrawStatus.COMPLETED) {
            throw new RuntimeException("Draw is not completed");
        }

        log.debug("Try to generate result for draw: {}", draw);

        LotteryType lotteryType = draw.getLotteryType();

        log.debug("Draw type: {}", lotteryType);

        LotteryCheckStrategy strategy = strategyResolver.resolve(lotteryType.getId());

        log.debug("Lotery strategy: {}", strategy);

        Set<Integer> winningNumbers = selectWinningNumbers(draw);

        log.debug("Winning numbers: {}", winningNumbers);

        /*DrawResult result = new DrawResult();
        result.setDraw(draw);
        result.setWinningCombination(
                winningNumbers.stream()
                        .sorted()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")));
        result.setResultTime(LocalDateTime.now());
        drawResultRepository.save(result);

        List<Ticket> tickets = ticketRepository.findByDraw(draw);
        for (Ticket ticket : tickets) {
            Set<Integer> userNumbers = parseNumbersFromJsonString(ticket.getData());
            boolean win = strategy.isWinning(userNumbers, winningNumbers);
            if (win) {
                log.debug("WINNNNN!!!!!!!!!!!!!!!");
            }
            ticket.setStatus(win ? Ticket.Status.WIN : Ticket.Status.LOSE);
            ticketRepository.save(ticket);
        }*/

        var result = drawResultFormer(draw, winningNumbers, strategy);

        log.debug("Try to save DrawResult");
        try {
            drawResultRepository.save(result);
        } catch (RuntimeException e) {
            log.error("Try to save DrawResult fault");
            throw new RuntimeException(e);
        }

        log.debug("Save DrawResult DONE!");

        return result;
    }

    private DrawResult drawResultFormer(Draw draw, Set<Integer> winsNumber, LotteryCheckStrategy strategy) {

        ObjectMapper jsonMapper = new ObjectMapper();
        DrawResult result = new DrawResult();
        List<Long> winningTickets = new ArrayList<>();

        result.setDraw(draw);
        result.setResultTime(LocalDateTime.now());
        result.setPrizePool(new BigDecimal(1000000)); // Гвозди-гвозди ))))

        try {
            result.setWinningCombination(jsonMapper.writeValueAsString(Map.of("numbers", List.of(winsNumber))));
        } catch (JsonProcessingException e) {
            log.error("Ooops! Convert winning combination to DrawResult fault");
            throw new RuntimeException(e);
        }

        List<Ticket> tickets = ticketRepository.findByDraw(draw);
        for (Ticket ticket : tickets) {
            log.debug("Ticket: {}", ticket);
            Set<Integer> userNumbers = parseNumbersFromJsonString(ticket.getData());
            boolean win = strategy.isWinning(userNumbers, winsNumber);
            // Работаем c БД
            ticket.setStatus(win ? Ticket.Status.WIN : Ticket.Status.LOSE);
            ticketRepository.save(ticket);
            // Отправляем сообщение по Telegram
            if (win) {
                log.debug("WIN ticket: {}", ticket);
                winningTickets.add(ticket.getId());
                try {
                    String userMessage = String.format("""
                                Тираж #%d завершён!
                                Ваш билет: %s
                                Выигрышная комбинация: %s
                                Результат: %s
                                """,
                            draw.getId(),
                            userNumbers.stream().sorted().map(String::valueOf).collect(Collectors.joining(",")),
                            result.getWinningCombination(),
                            ticket.getStatus()
                    );
                    Long chatId = Long.parseLong(ticket.getUser().getTelegram());
                    telegramNotificationService.sendTo(chatId, userMessage);
                } catch (NumberFormatException e) {
                    // лог, если телеграм id кривой
                    log.error(e.getMessage());
                }
            }

        }

        try {
            result.setWinningTickets(jsonMapper.writeValueAsString(Map.of("tickets", List.of(winningTickets))));
        } catch (JsonProcessingException e) {
            log.error("Ooops! Convert winning tickets to DrawResult fault");
            throw new RuntimeException(e);
        }

        return result;
    }

    private Set<Integer> selectWinningNumbers(Draw draw) {
        List<Ticket> tickets = ticketRepository.findByDraw(draw);

        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found for draw");
        }

        // Выбираем случайный билет как выигрышный
        Ticket randomTicket = tickets.get(random.nextInt(tickets.size()));
        return parseNumbersFromJsonString(randomTicket.getData());
    }

    private Set<Integer> parseNumbersFromJsonString(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonString);
            JsonNode numbersNode = rootNode.path("numbers");
            List<Integer> numbersList = new ArrayList<>();
            Iterator<JsonNode> elements = numbersNode.elements();
            while(elements.hasNext()) {
                Integer val = elements.next().asInt();
                numbersList.add(val);
            }
            return new HashSet<>(numbersList);
        } catch (Exception e) {
            log.error("Error parse ticket numbers");
            throw new RuntimeException("Failed to parse numbers from ticket data: " + jsonString, e);
        }
    }

    // Планировщик на случай автогенерации ночью (опционально)
    /*@Scheduled(cron = "0 0 0 * * ?")
    public void autoGenerateDrawResults() {
        List<Draw> draws = drawRepository.findByStatus(DrawStatus.COMPLETED.toString());
        for (Draw draw : draws) {
            boolean alreadyHasResult = drawResultRepository.existsByDraw(draw);
            if (!alreadyHasResult) {
                generateResult(draw.getId());
            }
        }
    }*/

    public Optional<DrawResult> getById(Long id) {
        return drawResultRepository.findById(id);
    }
}