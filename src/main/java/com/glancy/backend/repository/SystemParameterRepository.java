package com.glancy.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.glancy.backend.entity.SystemParameter;

/**
 * Repository for accessing {@link SystemParameter} values stored
 * in the database.
 */
@Repository
public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long> {
    Optional<SystemParameter> findByName(String name);
}
