package com.glancy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.glancy.backend.dto.LoginRequest;
import com.glancy.backend.dto.LoginResponse;
import com.glancy.backend.dto.LoginIdentifier;
import com.glancy.backend.dto.UserRegistrationRequest;
import com.glancy.backend.dto.UserStatisticsResponse;
import com.glancy.backend.dto.UserResponse;
import com.glancy.backend.dto.ThirdPartyAccountRequest;
import com.glancy.backend.dto.ThirdPartyAccountResponse;
import com.glancy.backend.dto.AvatarResponse;
import com.glancy.backend.dto.UsernameResponse;
import com.glancy.backend.dto.DailyActiveUserResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.entity.LoginDevice;
import com.glancy.backend.entity.ThirdPartyAccount;
import com.glancy.backend.repository.UserRepository;
import com.glancy.backend.repository.LoginDeviceRepository;
import com.glancy.backend.repository.ThirdPartyAccountRepository;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final AvatarStorage avatarStorage;

    public UserService(UserRepository userRepository,
                       LoginDeviceRepository loginDeviceRepository,
                       ThirdPartyAccountRepository thirdPartyAccountRepository,
                       AvatarStorage avatarStorage) {
        this.userRepository = userRepository;
        this.loginDeviceRepository = loginDeviceRepository;
        this.thirdPartyAccountRepository = thirdPartyAccountRepository;
        this.avatarStorage = avatarStorage;
    }

    /**
     * Register a new user ensuring username and email uniqueness.
     */
    @Transactional
    public UserResponse register(UserRegistrationRequest req) {
        log.info("Registering user {}", req.getUsername());
        if (userRepository.findByUsernameAndDeletedFalse(req.getUsername()).isPresent()) {
            log.warn("Username {} already exists", req.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userRepository.findByEmailAndDeletedFalse(req.getEmail()).isPresent()) {
            log.warn("Email {} is already in use", req.getEmail());
            throw new IllegalArgumentException("邮箱已被使用");
        }
        if (userRepository.findByPhoneAndDeletedFalse(req.getPhone()).isPresent()) {
            log.warn("Phone {} is already in use", req.getPhone());
            throw new IllegalArgumentException("手机号已被使用");
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
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * Authenticate a user and record login device information if provided.
     */
    @Transactional
    public LoginResponse login(LoginRequest req) {
        String account = req.getAccount();
        if (account == null || account.isEmpty()) {
            log.warn("No account provided for login");
            throw new IllegalArgumentException("用户名、邮箱或手机号必须填写其一");
        }

        LoginIdentifier.Type type = LoginIdentifier.resolveType(account);

        String identifier;
        User user;
        switch (type) {
            case EMAIL:
                identifier = account;
                final String email = identifier;
                user = userRepository.findByEmailAndDeletedFalse(email)
                        .orElseThrow(() -> {
                            log.warn("User with email {} not found or deleted", email);
                            return new IllegalArgumentException("用户不存在或已注销");
                        });
                break;
            case PHONE:
                identifier = account;
                String phone = identifier;
                if (!phone.startsWith("+")) {
                    phone = "+86" + phone;
                }
                final String lookupPhone = phone;
                final String raw = identifier;
                user = userRepository.findByPhoneAndDeletedFalse(lookupPhone)
                        .orElseGet(() -> userRepository.findByPhoneAndDeletedFalse(raw)
                                .orElseThrow(() -> {
                                    log.warn("User with phone {} not found or deleted", raw);
                                    return new IllegalArgumentException("用户不存在或已注销");
                                }));
                identifier = phone;
                break;
            case USERNAME:
            default:
                identifier = account;
                final String uname = identifier;
                user = userRepository.findByUsernameAndDeletedFalse(uname)
                        .orElseThrow(() -> {
                            log.warn("User {} not found or deleted", uname);
                            return new IllegalArgumentException("用户不存在或已注销");
                        });
                break;
        }

        log.info("Attempting login for {}", identifier);

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

        user.setLastLoginAt(LocalDateTime.now());
        String token = java.util.UUID.randomUUID().toString();
        user.setLoginToken(token);
        userRepository.save(user);

        log.info("User {} logged in", user.getId());
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getAvatar(), user.getPhone(), user.getMember(), token);
    }

    /**
     * Bind an external account (e.g. social login) to an existing user.
     */
    @Transactional
    public ThirdPartyAccountResponse bindThirdPartyAccount(Long userId, ThirdPartyAccountRequest req) {
        log.info("Binding {} account for user {}", req.getProvider(), userId);
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
        return new ThirdPartyAccountResponse(saved.getId(), saved.getProvider(),
                saved.getExternalId(), saved.getUser().getId());
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
     * Calculate today's active users and their rate among all users.
     */
    @Transactional(readOnly = true)
    public DailyActiveUserResponse getDailyActiveStats() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        long active = userRepository.countByDeletedFalseAndLastLoginAtAfter(start);
        long total = userRepository.countByDeletedFalse();
        double rate = total == 0 ? 0d : (double) active / total;
        return new DailyActiveUserResponse(active, rate);
    }

    /**
     * Count all active (non-deleted) users.
     */
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public void validateToken(Long userId, String token) {
        userRepository.findById(userId)
                .filter(u -> token != null && token.equals(u.getLoginToken()))
                .orElseThrow(() -> new IllegalArgumentException("无效的用户令牌"));
    }

    /**
     * Invalidate the login token for a user, effectively logging them out.
     */
    @Transactional
    public void logout(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (token == null || !token.equals(user.getLoginToken())) {
            throw new IllegalArgumentException("无效的用户令牌");
        }
        user.setLoginToken(null);
        userRepository.save(user);
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

    /**
     * Upload a new avatar image and update the user's record.
     */
    @Transactional
    public AvatarResponse uploadAvatar(Long userId, MultipartFile file) {
        try {
            String url = avatarStorage.upload(file);
            return updateAvatar(userId, url);
        } catch (IOException e) {
            log.error("Failed to upload avatar", e);
            throw new IllegalStateException("上传头像失败");
        }
    }

    /**
     * Update the username for the specified user.
     */
    @Transactional
    public UsernameResponse updateUsername(Long userId, String username) {
        log.info("Updating username for user {}", userId);
        userRepository.findByUsernameAndDeletedFalse(username).ifPresent(u -> {
            throw new IllegalArgumentException("用户名已存在");
        });
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setUsername(username);
        User saved = userRepository.save(user);
        return new UsernameResponse(saved.getUsername());
    }

    /**
     * Set a user as member.
     */
    @Transactional
    public void activateMembership(Long userId) {
        log.info("Activating membership for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setMember(true);
        userRepository.save(user);
    }

    /**
     * Remove member status from a user.
     */
    @Transactional
    public void removeMembership(Long userId) {
        log.info("Removing membership for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setMember(false);
        userRepository.save(user);
    }

}
