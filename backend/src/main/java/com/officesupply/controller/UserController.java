package com.officesupply.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.officesupply.dto.UserDto;
import com.officesupply.dto.ApiResponse;
import com.officesupply.service.UserService;
import com.officesupply.enums.UserRole;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("All users retrieved")
                .data(users)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable Long id) {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User retrieved")
                .data(user)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<?>> getUsersByRole(@PathVariable String role) {
        List<UserDto> users = userService.getUsersByRole(UserRole.valueOf(role));
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Users retrieved by role")
                .data(users)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
}
