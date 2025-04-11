package com.caloriecompass.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.caloriecompass.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.caloriecompass.infrastructure.persistence.repository")
public class DatabaseConfig {
}