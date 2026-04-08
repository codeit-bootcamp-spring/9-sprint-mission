package com.sprint.mission.discodeit.config; // 👈 패키지 경로가 맞는지 꼭 확인!

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // 시간 자동 기록
public class JpaAuditConfig {

}