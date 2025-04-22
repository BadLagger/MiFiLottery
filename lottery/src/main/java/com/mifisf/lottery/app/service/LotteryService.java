package com.mifisf.lottery.app.service;

import com.mifisf.lottery.app.entity.LotteryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mifisf.lottery.app.repository.LotteryRepository;

import java.util.List;

@Service
public class LotteryService {
    @Autowired
    private LotteryRepository repository;

    public List<LotteryEntity> findAll(){
        return repository.findAll();
    }

    public LotteryEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
