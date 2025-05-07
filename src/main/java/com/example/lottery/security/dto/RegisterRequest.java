package com.example.lottery.security.dto;

import com.example.lottery.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @NotNull(message = "Username is mandatory")
    private String name;
    @NotNull(message = "Password is mandatory")
    private String password;
    @NotNull(message = "Role is  mandatory")
    private String role;
    @NotNull(message = "Telegram Id is mandatory")
    private String telegram;
}