package com.glancy.backend.repository;

import com.glancy.backend.entity.ThirdPartyAccount;
import com.glancy.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ThirdPartyAccountRepositoryTest {

    @Autowired
    private ThirdPartyAccountRepository thirdPartyAccountRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByProviderAndExternalId() {
        User user = userRepository.save(TestEntityFactory.user(50));
        ThirdPartyAccount tpa = TestEntityFactory.thirdPartyAccount(user, "google", "ext123");
        thirdPartyAccountRepository.save(tpa);

        Optional<ThirdPartyAccount> found = thirdPartyAccountRepository.findByProviderAndExternalId("google", "ext123");
        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getUser().getId());
    }
}
