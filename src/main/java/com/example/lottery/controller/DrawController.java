package com.example.lottery.controller;

import com.example.lottery.dto.DrawRequestDto;
import com.example.lottery.dto.DrawStatus;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.DrawResult;
import com.example.lottery.service.DrawResultService;
import com.example.lottery.service.DrawService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DrawController {
    public final DrawService drawService;
    public final DrawResultService drawResultService;

    @Value("${app.default.draw-duration}")
    private Integer defaultDuration;

    @GetMapping("/draws/all")
    public List<Draw> getAllDraws() { return drawService.findAll();}

    @GetMapping("/draws/active")
    public List<Draw> getActiveDraws() { return drawService.findByStatus(DrawStatus.ACTIVE);}

    /* Get Active or Planned draws */
    @GetMapping("/draws/available")
    public List<Draw> getAvailableDraws() {
        var  result = drawService.findByStatus(DrawStatus.ACTIVE);
        result.addAll(drawService.findByStatus(DrawStatus.PLANNED));
        return result;
    }

    @GetMapping("/draws/{id}")
    public Draw getDrawsById(@PathVariable Long id) {

        var result = drawService.findById(id).orElseThrow(() ->
            new IllegalArgumentException(String.format("User with %d not found", id)));

        return result;
    }

    @GetMapping("/draws/{id}/results")
    public ResponseEntity<DrawResult> getDrawIdResults(@PathVariable Long id) {

        var result = drawResultService.getById(id);

        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping("/admin/draws")
    public ResponseEntity<Draw> createDraw(@Valid @RequestBody DrawRequestDto request) throws MethodArgumentNotValidException {

        // Проверка Duration
        if (request.duration() == null) {
            request = new DrawRequestDto(
                    request.name(),
                    request.lotteryTypeId(),
                    request.startTime(),
                    defaultDuration);
        } else {
            if (request.duration() <= 0) {
                throw new IllegalArgumentException("Duration should be positive");
            }
        }

        // Проверка на дублирующие значения тиража
        if (drawService.existsSameLotteryOnDay(request.lotteryTypeId(), request.startTime())) {
            throw new IllegalArgumentException("Draw with the same type exists in this day!");
        }

        System.out.println("Try to create draw");
        Draw draw = drawService.createDraw(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(draw);
    }

    @PutMapping("/admin/draws/{id}/cancel")
    public ResponseEntity<Draw> cancelDraw(@PathVariable Long id) {
        // Получаем существующий тираж по ID
        Optional<Draw> existingDrawOptional = drawService.findById(id);

        if (existingDrawOptional.isEmpty()) {
            System.out.println("NotFound");
            return ResponseEntity.notFound().build(); // Если тираж не найден, возвращаем 404
        }

        Draw existingDraw = existingDrawOptional.get();

        if (existingDraw.getStatus() == DrawStatus.CANCELLED || existingDraw.getStatus() == DrawStatus.COMPLETED) {
            throw new IllegalArgumentException("Draw already completed or cancelled");
        }
        // Меняем статус тиража на CANCELLED
        drawService.setCancel(existingDraw);

        return ResponseEntity.ok(existingDraw); // Возвращаем обновленную версию тиража
    }
}
