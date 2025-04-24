package com.example.lottery.controller;

import com.example.lottery.entity.LotteryType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.lottery.service.LotteryService;

import java.util.List;

@RestController

public class LotteryController {
    private final LotteryService service;

    public LotteryController(LotteryService service) {
        System.out.println("Start Lottery Controller");
        this.service = service;
    }

    @GetMapping("/lottery")
    public List<LotteryType> getAllLotteries() {
        return service.findAll();
    }
}
