package com.gestion.stock.repository;

import com.gestion.stock.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional <User> findByUserName(String userName);
    Optional <User> findByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    Optional<User> findByActivationToken(String token);
}
