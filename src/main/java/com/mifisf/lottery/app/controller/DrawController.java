package com.mifisf.lottery.app.controller;

import com.mifisf.lottery.app.entity.Draw;
import com.mifisf.lottery.app.entity.DrawResult;
import com.mifisf.lottery.app.service.DrawService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DrawController {
    public final DrawService drawService;

    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    @GetMapping("/draws/all")
    public List<Draw> getAllDraws() { return drawService.findAll();}

    @GetMapping("/draws/active")
    public List<Draw> getActiveDraws() { return drawService.findByStatus(Draw.DrawStatus.ACTIVE);}

    /* Get Active or Planned draws */
    @GetMapping("/draws/available")
    public List<Draw> getAvailableDraws() {
        var  result = drawService.findByStatus(Draw.DrawStatus.ACTIVE);
        result.addAll(drawService.findByStatus(Draw.DrawStatus.PLANNED));
        return result;
    }

    @GetMapping("/draws/{id}")
    public Draw getDrawsById(@PathVariable Long id) {
        return drawService.findById(id).orElse(null);
    }

    @GetMapping("/draws/{id}/results")
    public DrawResult getDrawIdResults(@PathVariable Long id) {
        return drawService.findResultByDrawId(id);
    }
}
