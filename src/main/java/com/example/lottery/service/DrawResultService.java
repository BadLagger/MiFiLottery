package com.example.lottery.service;

import com.example.lottery.dto.DrawStatus;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.DrawResult;
import com.example.lottery.entity.LotteryType;
import com.example.lottery.entity.Ticket;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.DrawResultRepository;
import com.example.lottery.repository.TicketRepository;
import com.example.lottery.repository.UserRepository;
import com.example.lottery.service.notificationService.TelegramNotificationService;
import com.example.lottery.strategy.LotteryCheckStrategy;
import com.example.lottery.strategy.LotteryCheckStrategyResolver;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrawResultService {

    private final DrawRepository drawRepository;
    private final DrawResultRepository drawResultRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
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

        LotteryType lotteryType = draw.getLotteryType();
        LotteryCheckStrategy strategy = strategyResolver.resolve(lotteryType.getId());

        Set<Integer> winningNumbers = selectWinningNumbers(draw);

        DrawResult result = new DrawResult();
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
            ticket.setStatus(win ? Ticket.Status.WIN : Ticket.Status.LOSE);
            ticketRepository.save(ticket);

        // Уведомление пользователя
            userRepository.findById(ticket.getUser().getId()).ifPresent(user -> {
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

                try {
                    Long chatId = Long.parseLong(user.getTelegram());
                    telegramNotificationService.sendTo(chatId, userMessage);
                } catch (NumberFormatException e) {
                    // лог, если телеграм id кривой
                }
            });
        }


        return result;
    }


    private Long getUserTelegramChatId(Long userId) {
        return userRepository.findById(userId)
                .map(user -> Long.valueOf(user.getTelegram())) // ⚠️ если в БД telegram хранится как строка
                .orElseThrow(() -> new RuntimeException("User not found for ID: " + userId));
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
            List<Integer> list = objectMapper.readValue(jsonString, new TypeReference<List>() {
            });
            return new HashSet<>(list);
        } catch (Exception e) {
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