package com.example.lottery.controller;

import com.example.lottery.dto.TicketDto;
import com.example.lottery.service.TicketService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@AllArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * Создаёт новый билет.
     *
     * @param ticketDto объект DTO с информацией о новом билете
     * @return созданный билет в виде DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDto createTicket(@RequestBody TicketDto ticketDto) throws BadRequestException {
        return ticketService.createTicket(ticketDto);
    }

    /**
     * Получает билет по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор билета
     * @return DTO объекта билета
     */
    @GetMapping("/{id}")
    public TicketDto getTicket(@PathVariable Long id) {
        return ticketService.findTicketById(id);
    }
}