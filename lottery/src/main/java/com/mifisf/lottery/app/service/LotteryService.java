package com.mifisf.lottery.app.service;

import com.mifisf.lottery.app.entity.LotteryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mifisf.lottery.app.repository.LotteryRepository;

import java.util.List;

@Service
public class LotteryService {
    @Autowired
    private LotteryRepository repository;

    public List<LotteryType> findAll(){
        return repository.findAll();
    }

    public LotteryType findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
