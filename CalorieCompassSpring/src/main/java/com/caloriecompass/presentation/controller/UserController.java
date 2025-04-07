package com.caloriecompass.presentation.controller;

import com.caloriecompass.application.dto.response.UserResponse;
import com.caloriecompass.domain.entity.User;
import com.caloriecompass.domain.service.AuthService;
import com.caloriecompass.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
public class UserController {
    
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = authService.getUserById(getUserIdFromAuthentication());
            
            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .build();
            
            model.addAttribute("user", userResponse);
        }
        
        return "index";
    }
    
    @GetMapping("/user")
    @ResponseBody
    @Operation(
        summary = "Get user profile", 
        description = "Get current user's profile information",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = authService.getUserById(getUserIdFromAuthentication());
        
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
        
        return ResponseEntity.ok(userResponse);
    }
    
    private Long getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Not authenticated");
        }
        
        String email = authentication.getName();
        return authService.authenticateUser(email, "").getId();
    }
}