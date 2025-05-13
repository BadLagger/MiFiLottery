package com.example.lottery.security.config;

import com.example.lottery.repository.UserRepository;
import com.example.lottery.security.filter.JwtAuthFilter;
import com.example.lottery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

  /* Отладочный бин для отладки без секьюрити
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }*/

  @Bean
  public SecurityFilterChain getSecurityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    //todo заменить на консоль постгрес, если надо
                    .requestMatchers(
                            "/auth/**",
                            "/h2-console/**",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    ).permitAll()
                    //юзеры
                    .requestMatchers(HttpMethod.GET,
                            "/api/draws/**",
                            "/api/tickets/**")
                    .hasAuthority("USER")

                    //админы
                    .requestMatchers(HttpMethod.PUT, "/api/admin/draws/**").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/admin/lottery-types").hasAuthority("ADMIN")

                    .anyRequest().authenticated()
            )
            .headers(headers -> headers
                    .defaultsDisabled()
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            )
            .httpBasic(withDefaults())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
  }


  /*@Bean
  public UserDetailsService userDetailsService(UserRepository repository) {
    return new CustomUserDetailsService(repository);
  }*/

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


}
