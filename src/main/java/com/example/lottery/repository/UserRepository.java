package com.example.lottery.repository;


import com.example.lottery.entity.User;
import com.example.lottery.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями в базе данных.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по имени. @param username Имя пользователя. @return Optional с пользователем, если найден.
     */
    Optional<User> findByUsername(String username);
    /**
     * Находит всех пользователей с указанной ролью. @param roleName Название роли (например, "USER"). @return Список пользователей.
     */
    List<User> findByRole_Name(String roleName);
    /**
     * Проверяет, существует ли пользователь с заданным именем. @param username Имя пользователя. @return true, если пользователь существует.
     */
    boolean existsByUsername(String username);
    /**
     * Находит роль USER. @return Optional с ролью USER.
     */
    @Query("SELECT r FROM Role r WHERE r.name = 'USER'")
    Optional<Role> findUserRole();

}

