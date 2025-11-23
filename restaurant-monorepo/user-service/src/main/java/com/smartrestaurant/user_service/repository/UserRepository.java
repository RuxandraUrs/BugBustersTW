package com.smartrestaurant.user_service.repository;

import com.smartrestaurant.user_service.entity.User;
import com.smartrestaurant.user_service.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByNameContainingIgnoreCase(String name);
    Long countByRole(Role role);
}
