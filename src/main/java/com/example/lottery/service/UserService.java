package com.example.lottery.service;

import com.example.lottery.entity.Role;
import com.example.lottery.entity.User;
import com.example.lottery.mapper.UserMapper;
import com.example.lottery.repository.UserRepository;
import com.example.lottery.security.dto.LoginRequest;
import com.example.lottery.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.lottery.repository.RoleRepository;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;// Правильное имя поля
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрирует нового пользователя. @param username Логин пользователя (должен быть уникальным).
     * @param telegram Telegram-ник в формате @username.
     * @param password Пароль (длина 5-20 символов).
     * @return Зарегистрированный пользователь.
     * @throws IllegalArgumentException Если логин занят или данные некорректны.
     */

    public void registerUser(RegisterRequest newUser) {
        if (userRepository.existsByName(newUser.getName())) {
            log.error("User {} already exists", newUser.getName());
            throw new IllegalArgumentException("Username already exists");
        }

        var newUserRole = roleRepository.findByName(newUser.getRole());
        if (newUserRole.isEmpty()) {
            throw new IllegalArgumentException("User with role null");
        }

        if (newUserRole.get().getName().equalsIgnoreCase(Role.ADMIN)) {
            var adminCount = userRepository.countByRole(newUserRole.get());
            log.debug("Admin count: {}", adminCount);
            if (adminCount > 0) {
                throw new IllegalArgumentException("User with Role ADMIN already exists");
            }
         }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        log.debug("Before mapper: {}, {}, {}", newUser.getName(), newUser.getRole(), newUser.getTelegram());
        User user = userMapper.toEntity(newUser, newUserRole.get());

        if (user.getRole() == null) {
                log.debug("Oops!!! Role is null after mapper");
                throw new IllegalArgumentException("Mapper get null role");
        }

        userRepository.save(user);
    }

    /**
     * Аутентифицирует пользователя.
     * @param username Логин пользователя
     * @param password Пароль
     * @return Аутентифицированный пользователь
     * @throws RuntimeException Если пользователь не найден или пароль неверен
     */

    public User login(LoginRequest inUser) {

        var user = userRepository.findByName(inUser.name());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(null);
        }

        if (!passwordEncoder.matches(inUser.password(), user.get().getPassword())) {
            throw new BadCredentialsException(null);
        }

        return user.get();

    }

    // Будет возвращать User без проверки пароля
    /*public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }*/

    // Проверяет пароль (возвращает true/false вместо исключения)
    /*public boolean checkPassword(String username, String rawPassword) {
        User user = getUserByUsername(username);
        return BCrypt.checkpw(rawPassword, user.getPassword());
    }*/

    /**
     * Возвращает текущий баланс пользователя.
     * @param userId ID пользователя
     * @return Значение баланса
     * @throws IllegalArgumentException Если пользователь не найден
     */

    /*public BigDecimal getBalance(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
             return user.getBalance();
    }*/

    /**
     * Списывает средства с баланса.
     * @param userId ID пользователя
     * @param amount Сумма (должна быть > 0 и <= текущему балансу)
     * @throws IllegalStateException Если недостаточно средств
     */

    /*public void subtractFromBalance(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive and not null");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getBalance() == null) {
            throw new IllegalStateException("User balance is not initialized");
        }

        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
    }*/

    /**
     * Пополняет баланс пользователя.
     * @param userId ID пользователя
     * @param amount Сумма (должна быть > 0)
     * @throws IllegalArgumentException Если сумма некорректна
     */

    /*public void addToBalance (Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
            BigDecimal newBalance = user.getBalance().add(amount);
            user.setBalance(newBalance);
            userRepository.save(user);
        }*/
    }




