package com.caloriecompass.domain.service;

import com.caloriecompass.domain.entity.User;

public interface AuthService {
    User registerUser(String email, String password, String name);

    User authenticateUser(String email, String password);

    User getUserById(Long id);

    User findUserByEmail(String email);
}