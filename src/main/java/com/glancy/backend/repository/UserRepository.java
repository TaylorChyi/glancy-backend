package com.glancy.backend.repository;

import com.glancy.backend.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link User} entities with helpers to check unique fields.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndDeletedFalse(String username);

    Optional<User> findByEmailAndDeletedFalse(String email);

    Optional<User> findByPhoneAndDeletedFalse(String phone);

    long countByDeletedTrue();

    long countByDeletedFalse();

    long countByDeletedFalseAndMemberTrue();

    long countByDeletedFalseAndLastLoginAtAfter(LocalDateTime time);

    Optional<User> findByLoginToken(String loginToken);
}
