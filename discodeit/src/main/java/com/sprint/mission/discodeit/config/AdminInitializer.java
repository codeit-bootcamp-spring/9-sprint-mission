package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${discodeit.admin.username}")
  private String adminUsername;

  @Value("${discodeit.admin.email}")
  private String adminEmail;

  @Value("${discodeit.admin.password}")
  private String adminPassword;

  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(Role.ADMIN)) {
      return; // 이미 있으면 스킵
    }
    try {
      User admin = new User(
          adminUsername,
          adminEmail,
          passwordEncoder.encode(adminPassword),
          null,
          Role.ADMIN
      );
      userRepository.save(admin);
    } catch (DataIntegrityViolationException e) {
      log.info("관리자 계정이 다른 인스턴스에서 이미 생성됨, 스킵합니다.");
    }
  }
}