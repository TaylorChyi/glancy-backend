package com.glancy.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.glancy.backend.dto.LoginRequest;
import com.glancy.backend.dto.LoginResponse;
import com.glancy.backend.dto.UserRegistrationRequest;
import com.glancy.backend.dto.UserResponse;
import com.glancy.backend.dto.ThirdPartyAccountRequest;
import com.glancy.backend.dto.ThirdPartyAccountResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest req) {
        UserResponse resp = userService.register(req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /**
     * 删除/注销用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取用户信息（包含逻辑删除状态）
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserRaw(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse resp = userService.login(req);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * 绑定第三方账号
     */
    @PostMapping("/{id}/third-party-accounts")
    public ResponseEntity<ThirdPartyAccountResponse> bindThirdParty(@PathVariable Long id,
                                               @Valid @RequestBody ThirdPartyAccountRequest req) {        ThirdPartyAccountResponse resp = userService.bindThirdPartyAccount(id, req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }
}
