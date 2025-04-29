package com.example.lottery.service;

import com.example.lottery.entity.Role;
import com.example.lottery.entity.User;
import com.example.lottery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.example.lottery.repository.RoleRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;// Правильное имя поля

    /**
     * Регистрирует нового пользователя. @param username Логин пользователя (должен быть уникальным).
     * @param telegram Telegram-ник в формате @username.
     * @param password Пароль (длина 5-20 символов).
     * @return Зарегистрированный пользователь.
     * @throws IllegalArgumentException Если логин занят или данные некорректны.
     */

    public User registerUser(String username, String telegram, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        Role userRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setTelegram(telegram);
        user.assignRole(userRole);

        return userRepository.save(user);
    }

    /**
     * Аутентифицирует пользователя.
     * @param username Логин пользователя
     * @param password Пароль
     * @return Аутентифицированный пользователь
     * @throws RuntimeException Если пользователь не найден или пароль неверен
     */

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User does not exist"));
        if (!BCrypt.checkpw(password, user.getPassword()))
            throw new IllegalArgumentException("Invalid password");
        user.updateLastLogin();
        return user;
    }

    // Будет возвращать User без проверки пароля
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Проверяет пароль (возвращает true/false вместо исключения)
    public boolean checkPassword(String username, String rawPassword) {
        User user = getUserByUsername(username);
        return BCrypt.checkpw(rawPassword, user.getPassword());
    }

    /**
     * Возвращает текущий баланс пользователя.
     * @param userId ID пользователя
     * @return Значение баланса
     * @throws IllegalArgumentException Если пользователь не найден
     */

    public BigDecimal getBalance(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
             return user.getBalance();
    }

    /**
     * Списывает средства с баланса.
     * @param userId ID пользователя
     * @param amount Сумма (должна быть > 0 и <= текущему балансу)
     * @throws IllegalStateException Если недостаточно средств
     */

    public void subtractFromBalance(Long userId, BigDecimal amount) {
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
    }

    /**
     * Пополняет баланс пользователя.
     * @param userId ID пользователя
     * @param amount Сумма (должна быть > 0)
     * @throws IllegalArgumentException Если сумма некорректна
     */

    public void addToBalance (Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
            BigDecimal newBalance = user.getBalance().add(amount);
            user.setBalance(newBalance);
            userRepository.save(user);
        }
    }




