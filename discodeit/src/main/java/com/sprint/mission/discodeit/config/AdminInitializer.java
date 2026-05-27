package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    if (!userRepository.existsByRole(Role.ADMIN)) {
      log.info("ADMIN 계정이 존재하지 않아 기본 어드민 계정을 초기화합니다.");

      String encodedPassword = passwordEncoder.encode("admin1234");
      User adminUser = new User("admin", "admin@discodeit.com", encodedPassword, null);
      adminUser.updateRole(Role.ADMIN);

      new UserStatus(adminUser, java.time.Instant.now());

      userRepository.save(adminUser);
      log.info("초기 어드민 계정 생성 완료: username=admin");
    }
  }
}
