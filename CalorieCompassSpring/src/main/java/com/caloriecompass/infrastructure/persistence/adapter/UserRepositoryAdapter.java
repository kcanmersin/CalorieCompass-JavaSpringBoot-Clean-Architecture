package com.caloriecompass.infrastructure.persistence.adapter;

import com.caloriecompass.domain.entity.User;
import com.caloriecompass.domain.repository.UserRepository;
import com.caloriecompass.infrastructure.persistence.entity.UserEntity;
import com.caloriecompass.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    
    @Override
    public User save(User user) {
        UserEntity userEntity = toUserEntity(user);
        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return toUser(savedEntity);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(this::toUser);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(this::toUser);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
    
    private UserEntity toUserEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.id = user.getId();
        entity.email = user.getEmail();
        entity.password = user.getPassword();
        entity.name = user.getName();
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        return entity;
    }
    
    private User toUser(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.id)
                .email(userEntity.email)
                .password(userEntity.password)
                .name(userEntity.name)
                .createdAt(userEntity.createdAt)
                .updatedAt(userEntity.updatedAt)
                .build();
    }
}