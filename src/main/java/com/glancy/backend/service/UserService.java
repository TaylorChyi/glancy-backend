package com.glancy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.glancy.backend.dto.LoginRequest;
import com.glancy.backend.dto.LoginResponse;
import com.glancy.backend.dto.UserRegistrationRequest;
import com.glancy.backend.dto.UserStatisticsResponse;
import com.glancy.backend.dto.UserResponse;
import com.glancy.backend.dto.ThirdPartyAccountRequest;
import com.glancy.backend.dto.ThirdPartyAccountResponse;
import com.glancy.backend.dto.AvatarResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.entity.LoginDevice;
import com.glancy.backend.entity.ThirdPartyAccount;
import com.glancy.backend.repository.UserRepository;
import com.glancy.backend.repository.LoginDeviceRepository;
import com.glancy.backend.repository.ThirdPartyAccountRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;

/**
 * Provides core user management operations such as registration,
 * login and third-party account binding.
 */
@Slf4j
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

    /**
     * Register a new user ensuring username and email uniqueness.
     */
    @Transactional
    public UserResponse register(UserRegistrationRequest req) {
        log.info("Registering user {}", req.getUsername());
        log.debug("Registering user {}", req.getUsername());
        if (userRepository.findByUsernameAndDeletedFalse(req.getUsername()).isPresent()) {
            log.warn("Username {} already exists", req.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userRepository.findByEmailAndDeletedFalse(req.getEmail()).isPresent()) {
            log.warn("Email {} is already in use", req.getEmail());
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

    /**
     * Logically delete a user account.
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user {}", id);
        log.debug("Deleting user {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    /**
     * Retrieve a user by id, regardless of deletion flag.
     */
    @Transactional(readOnly = true)
    public User getUserRaw(Long id) {
        log.info("Fetching user {}", id);
        log.debug("Fetching user {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * Authenticate a user and record login device information if provided.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        log.info("Attempting login for {}", req.getUsername() != null ? req.getUsername() : req.getEmail());
        String identifier = req.getUsername() != null ? req.getUsername() : req.getEmail();
        log.debug("Login attempt for {}", identifier);
        User user = null;

        if (req.getUsername() != null && !req.getUsername().isEmpty()) {
            user = userRepository.findByUsernameAndDeletedFalse(req.getUsername())
                    .orElseThrow(() -> {
                        log.warn("User {} not found or deleted", req.getUsername());
                        return new IllegalArgumentException("用户不存在或已注销");
                    });
        } else if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            user = userRepository.findByEmailAndDeletedFalse(req.getEmail())
                    .orElseThrow(() -> {
                        log.warn("User with email {} not found or deleted", req.getEmail());
                        return new IllegalArgumentException("用户不存在或已注销");
                    });
        } else {
            log.warn("Username or email must be provided for login");
            throw new IllegalArgumentException("用户名或邮箱必须填写其一");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Password mismatch for user {}", user.getUsername());
            throw new IllegalArgumentException("密码错误");
        }

        if (req.getDeviceInfo() != null && !req.getDeviceInfo().isEmpty()) {
            LoginDevice device = new LoginDevice();
            device.setUser(user);
            device.setDeviceInfo(req.getDeviceInfo());
            loginDeviceRepository.save(device);

            List<LoginDevice> devices =
                    loginDeviceRepository.findByUserIdOrderByLoginTimeAsc(user.getId());
            if (devices.size() > 3) {
                for (int i = 0; i < devices.size() - 3; i++) {
                    loginDeviceRepository.delete(devices.get(i));
                }
            }
        }

        log.info("User {} logged in", user.getId());
        log.debug("User {} logged in", user.getId());
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getAvatar(), user.getPhone());
    }

    /**
     * Bind an external account (e.g. social login) to an existing user.
     */
    @Transactional
    public ThirdPartyAccountResponse bindThirdPartyAccount(Long userId, ThirdPartyAccountRequest req) {
        log.info("Binding {} account for user {}", req.getProvider(), userId);
        log.debug("Binding third-party account {}:{} to user {}", req.getProvider(),
                req.getExternalId(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", userId);
                    return new IllegalArgumentException("用户不存在");
                });

        thirdPartyAccountRepository
                .findByProviderAndExternalId(req.getProvider(), req.getExternalId())
                .ifPresent(a -> {
                    log.warn("Third-party account {}:{} already bound", req.getProvider(), req.getExternalId());
                    throw new IllegalArgumentException("该第三方账号已绑定");
                });

        ThirdPartyAccount account = new ThirdPartyAccount();
        account.setUser(user);
        account.setProvider(req.getProvider());
        account.setExternalId(req.getExternalId());
        ThirdPartyAccount saved = thirdPartyAccountRepository.save(account);
        log.debug("Bound account {}:{} to user {}", saved.getProvider(), saved.getExternalId(), userId);
        return new ThirdPartyAccountResponse(saved.getId(), saved.getProvider(),
                saved.getExternalId(), saved.getUser().getId());
    }

    /**
     * Check whether a user has purchased membership.
     */
    @Transactional(readOnly = true)
    public boolean isMember(Long userId) {
        log.info("Checking membership status for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return Boolean.TRUE.equals(user.getMember());
    }
  
    /**
     * Gather statistics about all user accounts.
     */
    @Transactional(readOnly = true)
    public UserStatisticsResponse getStatistics() {
        long total = userRepository.count();
        long deleted = userRepository.countByDeletedTrue();
        long members = userRepository.countByDeletedFalseAndMemberTrue();
        return new UserStatisticsResponse(total, members, deleted);
    }

    /**
     * Retrieve only the avatar URL of a user.
     */
    @Transactional(readOnly = true)
    public AvatarResponse getAvatar(Long userId) {
        log.info("Fetching avatar for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        return new AvatarResponse(user.getAvatar());
    }

    /**
     * Update the avatar URL for the specified user.
     */
    @Transactional
    public AvatarResponse updateAvatar(Long userId, String avatar) {
        log.info("Updating avatar for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setAvatar(avatar);
        User saved = userRepository.save(user);
        return new AvatarResponse(saved.getAvatar());
    }
}
