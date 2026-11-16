package com.findit.repository;

import com.findit.entity.User;
import com.findit.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByResetPasswordToken(String token);

    Page<User> findByRole(Role role, Pageable pageable);

    long countByActiveTrue();

    long countByRole(Role role);
}
