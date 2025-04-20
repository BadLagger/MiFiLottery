package com.mifisf.lottery.app.controllers;

import com.mifisf.lottery.app.models.LotteryEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mifisf.lottery.app.services.LotteryService;

import java.util.List;

@RestController
public class LotteryController {
    private final LotteryService service;

    public LotteryController(LotteryService service) {
        System.out.println("Start Lottery Controller");
        this.service = service;
    }

    @GetMapping("/lottery")
    public List<LotteryEntity> getAllLotteries() {
        return service.findAll();
    }
}
