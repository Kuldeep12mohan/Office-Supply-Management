package com.officesupply.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.officesupply.model.User;
import com.officesupply.enums.UserRole;
import com.officesupply.exception.ResourceNotFoundException;
import com.officesupply.repository.UserRepository;
import com.officesupply.dto.UserDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String username, String email, String fullName, 
                          String password, UserRole role) {
        log.info("Creating user: {}", username);
        
        User user = User.builder()
                .username(username)
                .email(email)
                .fullName(fullName)
                .password(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void disableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
        log.info("User disabled: {}", user.getUsername());
    }

    public void enableUser(Long id) {
        User user = getUserById(id);
        user.setEnabled(true);
        userRepository.save(user);
        log.info("User enabled: {}", user.getUsername());
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
