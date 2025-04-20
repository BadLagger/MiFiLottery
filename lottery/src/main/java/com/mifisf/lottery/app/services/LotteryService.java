package com.mifisf.lottery.app.services;

import com.mifisf.lottery.app.models.LotteryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mifisf.lottery.app.repositories.LotteryRepository;

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
