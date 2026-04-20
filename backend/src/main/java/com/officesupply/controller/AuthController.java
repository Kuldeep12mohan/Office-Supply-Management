package com.officesupply.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.officesupply.model.User;
import com.officesupply.dto.LoginRequest;
import com.officesupply.dto.LoginResponse;
import com.officesupply.dto.ApiResponse;
import com.officesupply.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request, HttpSession session) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().name());

            LoginResponse response = LoginResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .role(user.getRole().getDisplayName())
                    .message("Login successful")
                    .build();

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .data(response)
                    .timestamp(LocalDateTime.now().toString())
                    .build());

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Invalid username or password")
                            .timestamp(LocalDateTime.now().toString())
                            .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpSession session) {
        session.invalidate();
        log.info("User logged out");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Logged out successfully")
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Not authenticated")
                            .timestamp(LocalDateTime.now().toString())
                            .build());
        }
        
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User retrieved")
                .data(user)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }
}
