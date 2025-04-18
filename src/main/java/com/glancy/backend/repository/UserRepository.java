package com.glancy.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glancy.backend.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndDeletedFalse(String username);
    Optional<User> findByEmailAndDeletedFalse(String email);
}