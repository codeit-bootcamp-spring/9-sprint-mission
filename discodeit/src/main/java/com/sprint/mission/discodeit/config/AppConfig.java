package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableJpaAuditing
@EnableConfigurationProperties(AdminAccountInitializer.AdminProperties.class)
public class AppConfig {

}
