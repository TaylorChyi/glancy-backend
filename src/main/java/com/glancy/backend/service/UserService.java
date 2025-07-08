package com.glancy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glancy.backend.dto.LoginRequest;
import com.glancy.backend.dto.LoginResponse;
import com.glancy.backend.dto.UserRegistrationRequest;
import com.glancy.backend.dto.UserResponse;
import com.glancy.backend.dto.ThirdPartyAccountRequest;
import com.glancy.backend.dto.ThirdPartyAccountResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.entity.LoginDevice;
import com.glancy.backend.entity.ThirdPartyAccount;
import com.glancy.backend.repository.UserRepository;
import com.glancy.backend.repository.LoginDeviceRepository;
import com.glancy.backend.repository.ThirdPartyAccountRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LoginDeviceRepository loginDeviceRepository;
    private final ThirdPartyAccountRepository thirdPartyAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository,
                       LoginDeviceRepository loginDeviceRepository,
                       ThirdPartyAccountRepository thirdPartyAccountRepository) {
        this.userRepository = userRepository;
        this.loginDeviceRepository = loginDeviceRepository;
        this.thirdPartyAccountRepository = thirdPartyAccountRepository;
    }

    @Transactional
    public UserResponse register(UserRegistrationRequest req) {
        if (userRepository.findByUsernameAndDeletedFalse(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userRepository.findByEmailAndDeletedFalse(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("邮箱已被使用");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setAvatar(req.getAvatar());
        user.setPhone(req.getPhone());
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail(),
                saved.getAvatar(), saved.getPhone());
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserRaw(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        User user = null;

        if (req.getUsername() != null && !req.getUsername().isEmpty()) {
            user = userRepository.findByUsernameAndDeletedFalse(req.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在或已注销"));
        } else if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            user = userRepository.findByEmailAndDeletedFalse(req.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在或已注销"));
        } else {
            throw new IllegalArgumentException("用户名或邮箱必须填写其一");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        if (req.getDeviceInfo() != null && !req.getDeviceInfo().isEmpty()) {
            LoginDevice device = new LoginDevice();
            device.setUser(user);
            device.setDeviceInfo(req.getDeviceInfo());
            loginDeviceRepository.save(device);
        }

        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getAvatar(), user.getPhone());
    }

    @Transactional
    public ThirdPartyAccountResponse bindThirdPartyAccount(Long userId, ThirdPartyAccountRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        thirdPartyAccountRepository
                .findByProviderAndExternalId(req.getProvider(), req.getExternalId())
                .ifPresent(a -> {
                    throw new IllegalArgumentException("该第三方账号已绑定");
                });

        ThirdPartyAccount account = new ThirdPartyAccount();
        account.setUser(user);
        account.setProvider(req.getProvider());
        account.setExternalId(req.getExternalId());        ThirdPartyAccount saved = thirdPartyAccountRepository.save(account);
        return new ThirdPartyAccountResponse(saved.getId(), saved.getProvider(),
                saved.getExternalId(), saved.getUser().getId());
    }
}
