package com.glancy.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glancy.backend.dto.LoginRequest;
import com.glancy.backend.dto.LoginResponse;
import com.glancy.backend.dto.UserRegistrationRequest;
import com.glancy.backend.dto.UserResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail());
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

        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}