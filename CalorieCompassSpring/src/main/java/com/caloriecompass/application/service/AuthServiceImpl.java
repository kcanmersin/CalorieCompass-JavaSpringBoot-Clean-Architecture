package com.caloriecompass.application.service;

import com.caloriecompass.application.exception.InvalidCredentialsException;
import com.caloriecompass.application.exception.UserAlreadyExistsException;
import com.caloriecompass.domain.entity.User;
import com.caloriecompass.domain.repository.UserRepository;
import com.caloriecompass.domain.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public User registerUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        return user;
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}