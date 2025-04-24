package com.example.lottery.controller;

import com.example.lottery.dto.DrawRequestDto;
import com.example.lottery.dto.DrawResultDto;
import com.example.lottery.dto.DrawStatus;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.DrawResult;
import com.example.lottery.service.DrawService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DrawController {
    public final DrawService drawService;

    @Value("${app.default.draw-duration}")
    private Integer defaultDuration;

    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

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
        return drawService.findById(id).orElse(null);
    }

    @GetMapping("/draws/{id}/results")
    public DrawResult getDrawIdResults(@PathVariable Long id) {
        return drawService.findResultByDrawId(id);
    }

    @PostMapping("/admin/draws")
    public ResponseEntity<Draw> createDraw(@Valid @RequestBody DrawRequestDto request) throws MethodArgumentNotValidException {

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

        Draw draw= drawService.createDraw(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(draw);
    }

    // Обработчик ошибок
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArguments(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    // Обработчик ошибок
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<String> errors = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

}
