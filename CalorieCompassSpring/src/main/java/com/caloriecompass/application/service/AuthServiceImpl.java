package com.caloriecompass.application.service;

import com.caloriecompass.application.exception.InvalidCredentialsException;
import com.caloriecompass.application.exception.UserAlreadyExistsException;
import com.caloriecompass.domain.entity.User;
import com.caloriecompass.domain.repository.UserRepository;
import com.caloriecompass.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(String email, String password, String name) {
        log.debug("registerUser called with email: {}, name: {}", email, name);
        log.debug("Password length: {}", password != null ? password.length() : 0);

        // Validate input
        if (email == null || email.trim().isEmpty()) {
            log.error("Registration failed: Email is null or empty");
            throw new IllegalArgumentException("Email is required");
        }

        if (password == null || password.trim().isEmpty()) {
            log.error("Registration failed: Password is null or empty");
            throw new IllegalArgumentException("Password is required");
        }

        if (name == null || name.trim().isEmpty()) {
            log.error("Registration failed: Name is null or empty");
            throw new IllegalArgumentException("Name is required");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            log.error("Registration failed: User with email {} already exists", email);
            throw new UserAlreadyExistsException(email);
        }

        LocalDateTime now = LocalDateTime.now();

        log.debug("Creating new user entity for email: {}", email);
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User savedUser = userRepository.save(user);
        log.debug("User saved successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticateUser(String email, String password) {
        log.debug("authenticateUser called with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Authentication failed: User not found with email: {}", email);
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Authentication failed: Invalid password for user: {}", email);
            throw new InvalidCredentialsException();
        }

        log.debug("Authentication successful for user: {}", email);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.debug("getUserById called with ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
    }
}