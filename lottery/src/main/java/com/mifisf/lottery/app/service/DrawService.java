package com.mifisf.lottery.app.service;

import com.mifisf.lottery.app.entity.Draw;
import com.mifisf.lottery.app.entity.DrawResult;
import com.mifisf.lottery.app.repository.DrawRepository;
import com.mifisf.lottery.app.repository.DrawResultRepository;
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
