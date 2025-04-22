package com.mifisf.lottery.app.service;

import com.mifisf.lottery.app.entity.Draw;
import com.mifisf.lottery.app.repository.DrawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrawService {
    @Autowired
    private DrawRepository repository;

    public List<Draw> findAll() {return repository.findAll();}

    public List<Draw> findByStatus(Draw.DrawStatus status) {
        return repository.findByStatus(status.toString());
    }
}
