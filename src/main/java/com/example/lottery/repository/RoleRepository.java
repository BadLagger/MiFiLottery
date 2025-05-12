package com.example.lottery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.lottery.entity.Role;
import java.util.Optional;


/**
 * Репозиторий для работы с ролями в базе данных.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Находит роль по названию. @param name Название роли (например, "USER"). @return Optional с ролью, если найдена.
     */
    Optional<Role> findByName(String name);
}
