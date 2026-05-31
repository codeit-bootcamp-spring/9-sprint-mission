package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {


  @Value("${discodeit.admin.username:}")
  private String adminUsername;

  @Value("${discodeit.admin.email:}")
  private String adminEmail;

  @Value("${discodeit.admin.password:}")
  private String adminPassword;

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (!userRepository.existsByRole(Role.ADMIN)) {
      User admin = new User(
          adminUsername,
          passwordEncoder.encode(adminPassword),
          adminEmail,
          null
      );
      admin.updateRole(Role.ADMIN);
      userRepository.save(admin);
      log.info("초기 어드민 계정 생성 완료 (username: admin)");
    }
  }
}
