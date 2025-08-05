package com.glancy.backend.repository;

import com.glancy.backend.entity.LoginDevice;
import com.glancy.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LoginDeviceRepositoryTest {

    @Autowired
    private LoginDeviceRepository loginDeviceRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserIdOrderByLoginTimeAsc() {
        User user = userRepository.save(TestEntityFactory.user(30));
        LoginDevice d1 = TestEntityFactory.loginDevice(user, "d1", LocalDateTime.now());
        LoginDevice d2 = TestEntityFactory.loginDevice(user, "d2", LocalDateTime.now().plusMinutes(1));
        loginDeviceRepository.save(d2);
        loginDeviceRepository.save(d1);

        List<LoginDevice> list = loginDeviceRepository.findByUserIdOrderByLoginTimeAsc(user.getId());
        assertEquals("d1", list.get(0).getDeviceInfo());
        assertEquals(2, list.size());
    }
}
