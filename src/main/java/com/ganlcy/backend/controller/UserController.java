package com.ganlcy.backend.controller;

import com.ganlcy.backend.dto.UserRegistrationRequest;
import com.ganlcy.backend.dto.UserResponse;
import com.ganlcy.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}