package com.charizad.compiled.gamification_service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository")
public class MongoConfig {
}
