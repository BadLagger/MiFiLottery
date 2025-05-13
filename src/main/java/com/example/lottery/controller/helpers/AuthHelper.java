package com.example.lottery.controller.helpers;

import com.example.lottery.entity.User;
import com.example.lottery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelper {

  private final UserService userService;

  public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userService.findByUsername(auth.getName());
  }

  public Long getCurrentUserId() {
    return getCurrentUser().getId();
  }
}
