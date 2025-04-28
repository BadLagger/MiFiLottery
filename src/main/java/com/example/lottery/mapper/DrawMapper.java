package com.example.lottery.mapper;

import com.example.lottery.dto.DrawDto;
import com.example.lottery.dto.DrawRequestDto;
import com.example.lottery.dto.DrawResultDto;
import com.example.lottery.dto.DrawStatus;
import com.example.lottery.entity.Draw;
import com.example.lottery.entity.LotteryType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DrawMapper {
    /*DrawDto toDto(Draw draw);
    List<DrawDto> toDtoList(List<Draw> draws);

    Draw toEntity(DrawDto drawDto);*/
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lotteryType", ignore = true)
    Draw toEntity(DrawRequestDto drawRequestDto);

    default Draw toEntity(DrawRequestDto request, LotteryType lotteryType, DrawStatus status) {
        Draw draw = toEntity(request);
        draw.setLotteryType(lotteryType);
        draw.setStatus(status);
        return draw;
    }
}
