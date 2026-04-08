package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 애플리케이션 공통 설정.
 * JPA Auditing을 활성화하여 @CreatedDate, @LastModifiedDate를 자동 처리한다.
 */
@Configuration
@EnableJpaAuditing
public class AppConfig {

}
