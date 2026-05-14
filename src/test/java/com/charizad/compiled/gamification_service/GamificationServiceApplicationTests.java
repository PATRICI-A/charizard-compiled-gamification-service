package com.charizad.compiled.gamification_service;

import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.NotificationEventPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=" +
		"org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration," +
		"org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration," +
		"org.springframework.boot.data.mongodb.autoconfigure.DataMongoRepositoriesAutoConfiguration"
})
@Import(GamificationServiceApplicationTests.TestMongoMockConfig.class)
@SpringJUnitConfig
class GamificationServiceApplicationTests {

	@org.springframework.context.annotation.Configuration
	static class TestMongoMockConfig {

		@Bean
		BadgeRepositoryPort badgeRepositoryPort() {
			return Mockito.mock(BadgeRepositoryPort.class);
		}

		@Bean
		UserGamificationRepositoryPort userGamificationRepositoryPort() {
			return Mockito.mock(UserGamificationRepositoryPort.class);
		}

		@Bean
		NotificationEventPort notificationEventPort() {
			return Mockito.mock(NotificationEventPort.class);
		}
	}

	@Test
	void contextLoads() {
	}

}
