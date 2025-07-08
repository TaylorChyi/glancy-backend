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
import com.glancy.backend.dto.AvatarRequest;
import com.glancy.backend.dto.AvatarResponse;
import com.glancy.backend.entity.User;
import com.glancy.backend.service.UserService;

/**
 * User management endpoints including registration, login and
 * third-party account binding.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user account.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest req) {
        UserResponse resp = userService.register(req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /**
     * Delete (logically) an existing user account.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Fetch user information regardless of deletion status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserRaw(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Authenticate a user with username/email and password.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse resp = userService.login(req);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * Bind a third-party account to the specified user.
     */
    @PostMapping("/{id}/third-party-accounts")
    public ResponseEntity<ThirdPartyAccountResponse> bindThirdParty(
            @PathVariable Long id,
            @Valid @RequestBody ThirdPartyAccountRequest req) {
        ThirdPartyAccountResponse resp = userService.bindThirdPartyAccount(id, req);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    /**
     * Get the avatar URL for a specific user.
     */
    @GetMapping("/{id}/avatar")
    public ResponseEntity<AvatarResponse> getAvatar(@PathVariable Long id) {
        AvatarResponse resp = userService.getAvatar(id);
        return ResponseEntity.ok(resp);
    }

    /**
     * Update the avatar URL for a user.
     */
    @PutMapping("/{id}/avatar")
    public ResponseEntity<AvatarResponse> updateAvatar(
            @PathVariable Long id,
            @Valid @RequestBody AvatarRequest req) {
        AvatarResponse resp = userService.updateAvatar(id, req.getAvatar());
        return ResponseEntity.ok(resp);
    }
}
