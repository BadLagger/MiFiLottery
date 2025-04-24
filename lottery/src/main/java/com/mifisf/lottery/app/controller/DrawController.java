package com.mifisf.lottery.app.controller;

import com.mifisf.lottery.app.entity.Draw;
import com.mifisf.lottery.app.service.DrawService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DrawController {
    public final DrawService service;

    public DrawController(DrawService service) {
        this.service = service;
    }

    @GetMapping("/draws/all")
    public List<Draw> getAllDraws() { return service.findAll();}

    @GetMapping("/draws/active")
    public List<Draw> getActiveDraws() { return service.findByStatus(Draw.DrawStatus.ACTIVE);}

    /* Get Active or Planned draws */
    @GetMapping("/draws/available")
    public List<Draw> getAvailableDraws() {
        var  result = service.findByStatus(Draw.DrawStatus.ACTIVE);
        result.addAll(service.findByStatus(Draw.DrawStatus.PLANNED));
        return result;
    }
}
