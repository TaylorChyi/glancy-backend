package com.glancy.backend.repository;

import com.glancy.backend.entity.User;
import com.glancy.backend.entity.UserProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserProfileRepositoryTest {

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserId() {
        User user = userRepository.save(TestEntityFactory.user(60));
        UserProfile profile = TestEntityFactory.userProfile(user);
        userProfileRepository.save(profile);

        Optional<UserProfile> found = userProfileRepository.findByUserId(user.getId());
        assertTrue(found.isPresent());
        assertEquals(20, found.get().getAge());
    }
}
