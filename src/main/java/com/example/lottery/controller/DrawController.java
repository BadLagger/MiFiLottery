package com.example.lottery.controller;

import com.example.lottery.dto.DrawDto;
import com.example.lottery.entity.Draw;
import com.example.lottery.mapper.DrawMapper;
import com.example.lottery.service.DrawService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Draws", description = "Управление лотерейными тиражами")
public class DrawController {

    private final DrawService drawService;
    private final DrawMapper drawMapper;


    @GetMapping
    public String checkWork(){
        return "Работает";
    }

    @PostMapping
    public ResponseEntity<DrawDto> checkPostWork(@RequestBody DrawDto drawDto){
         Draw saved = drawService.createDraw(drawMapper.toEntity(drawDto));
         return ResponseEntity.ok(drawMapper.toDto(saved));
    }

    @PostMapping("/admin/draws")
    public ResponseEntity<DrawDto> createDraw(@RequestBody DrawDto drawDto) {
        // Draw saved = drawService.create(drawMapper.toEntity(drawDto));
       // return ResponseEntity.ok(drawMapper.toDto(saved));
        return null;
        //todo вернуть нормальный return
    }

    @GetMapping("/draws/active")
    public ResponseEntity<List<DrawDto>> getActiveDraws() {
       // List<Draw> draws = drawService.getActive();
        //return ResponseEntity.ok(drawMapper.toDtoList(draws));
        return null;
        //todo вернуть нормальный return
    }

    @PutMapping("/admin/draws/{id}/cancel")
    public ResponseEntity<Void> cancelDraw(@PathVariable Long id) {
       // drawService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/draws/{id}/results")
    public ResponseEntity<String> getResults(@PathVariable Long id) {
        return ResponseEntity.ok("Mock result for draw " + id);
    }
}
