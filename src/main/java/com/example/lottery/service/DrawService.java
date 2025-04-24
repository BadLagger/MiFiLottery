package com.example.lottery.service;

import com.example.lottery.entity.Draw;
import com.example.lottery.entity.DrawResult;
import com.example.lottery.repository.DrawRepository;
import com.example.lottery.repository.DrawResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DrawService {
    @Autowired
    private DrawRepository drawRepository;

    @Autowired
    private DrawResultRepository drawResultRepository;

    public List<Draw> findAll() {return drawRepository.findAll();}

    public List<Draw> findByStatus(Draw.DrawStatus status) {
        return drawRepository.findByStatus(status.toString());
    }

    public Optional<Draw> findById(Long id) { return drawRepository.findById(id); }

    public DrawResult findResultByDrawId(Long id) {
        return  drawResultRepository.findByDraw(drawRepository.findById(id).orElse(null));
    }
}
