package com.caloriecompass.presentation.controller;

import com.caloriecompass.application.dto.request.UserLoginRequest;
import com.caloriecompass.application.dto.request.UserSignupRequest;
import com.caloriecompass.application.dto.response.AuthResponse;
import com.caloriecompass.application.dto.response.UserResponse;
import com.caloriecompass.domain.entity.User;
import com.caloriecompass.domain.service.AuthService;
import com.caloriecompass.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/login")
    public String showLoginPage() {
        log.debug("Showing login page");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        log.debug("Showing registration page");
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        log.debug("Processing logout request");
        Cookie cookie = new Cookie("auth_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute UserLoginRequest loginRequest,
            HttpServletResponse response, Model model) {
        log.debug("Processing login request for email: {}", loginRequest.getEmail());
        try {
            User user = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());

            log.debug("Login successful for user: {}", user.getEmail());

            Cookie cookie = new Cookie("auth_token", token);
            cookie.setMaxAge(24 * 60 * 60); // 24 hours
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return "redirect:/";
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute UserSignupRequest signupRequest,
            HttpServletResponse response, Model model) {
        log.debug("Processing registration request for email: {} with name: {}",
                signupRequest.getEmail(), signupRequest.getName());
        log.debug("Password length: {}",
                signupRequest.getPassword() != null ? signupRequest.getPassword().length() : 0);

        try {
            log.debug("Attempting to register user with AuthService");
            User user = authService.registerUser(
                    signupRequest.getEmail(),
                    signupRequest.getPassword(),
                    signupRequest.getName());

            log.debug("Registration successful for user ID: {}, email: {}", user.getId(), user.getEmail());
            String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());

            Cookie cookie = new Cookie("auth_token", token);
            cookie.setMaxAge(24 * 60 * 60); // 24 hours
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return "redirect:/";
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/auth/login")
    @ResponseBody
    @Operation(summary = "Login user", description = "Login a user and return token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        log.debug("API login request for email: {}", loginRequest.getEmail());
        User user = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        log.debug("API login successful for user ID: {}", user.getId());

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/auth/sign-up")
    @ResponseBody
    @Operation(summary = "Register user", description = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserSignupRequest signupRequest) {
        log.debug("API registration request for email: {}", signupRequest.getEmail());
        User user = authService.registerUser(
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getName());

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        log.debug("API registration successful for user ID: {}", user.getId());

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }
}