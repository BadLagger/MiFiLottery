package com.example.lottery.mapper;

import com.example.lottery.entity.Role;
import com.example.lottery.entity.User;
import com.example.lottery.security.dto.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "role", expression = "java(convertRoleNameToRole(requestDto.getRole()))")
    User toEntity(RegisterRequest requestDto);

    default Role convertRoleNameToRole(String role) {
        if (role == null) {
            return null;
        }
        return new Role(role);
    }

    default User toEntity(RegisterRequest requestDto, Role role) {
        User user = toEntity(requestDto);
        user.setRole(role);
        return user;
    }
}
