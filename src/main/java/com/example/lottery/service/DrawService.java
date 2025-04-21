package com.example.lottery.service;

import com.example.lottery.entity.Draw;
import com.example.lottery.repository.DrawRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DrawService {
//    private final DrawRepository drawRepository;
//
//    public DrawService(DrawRepository drawRepository) {
//        this.drawRepository = drawRepository;
//    }
//
//    public List<Draw> getActiveDraws() {
//        return drawRepository.findByStatus(Draw.Status.ACTIVE);
//    }
//

    private final AtomicLong idGenerator = new AtomicLong(1);
    public Draw createDraw(Draw draw) {
        //return drawRepository.save(draw);
        draw.setId(idGenerator.getAndIncrement());
        if (draw.getStatus() == null) {
            draw.setStatus(Draw.Status.PLANNED);
        }
        if (draw.getStartTime() == null) {
            draw.setStartTime(LocalDateTime.now().plusMinutes(5));
        }
        return draw;
    }
}
