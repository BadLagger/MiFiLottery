package com.example.lottery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class AppConfig {
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule()); // Для работы с LocalDateTime
    return mapper;
  }

  @Bean
  public SecureRandom secureRandom() {
    SecureRandom random = new SecureRandom();
    // Можно добавить дополнительную настройку
    // random.setSeed(...);
    return random;
  }
}
