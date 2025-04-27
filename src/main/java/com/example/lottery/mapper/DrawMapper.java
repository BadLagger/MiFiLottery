package com.example.lottery.mapper;

import com.example.lottery.dto.DrawDto;
import com.example.lottery.entity.Draw;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DrawMapper {
    DrawDto toDto(Draw draw);
    List<DrawDto> toDtoList(List<Draw> draws);

    Draw toEntity(DrawDto drawDto);
}