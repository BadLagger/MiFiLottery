package com.example.lottery.mapper;

import com.example.lottery.entity.User;
import com.example.lottery.security.dto.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    User toEntity(RegisterRequest requestDto);
}
