package com.officesupply.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.officesupply.model.User;
import com.officesupply.enums.UserRole;
import com.officesupply.exception.ResourceNotFoundException;
import com.officesupply.repository.UserRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashed_password")
                .fullName("Test User")
                .role(UserRole.EMPLOYEE)
                .enabled(true)
                .build();
    }

    @Test
    void testCreateUser() {
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("testuser", "test@example.com", "Test User", "password123", UserRole.EMPLOYEE);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getFullName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testDisableUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.disableUser(1L);

        verify(userRepository, times(1)).save(any(User.class));
    }
}
