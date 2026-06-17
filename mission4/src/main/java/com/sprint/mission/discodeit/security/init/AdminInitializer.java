package com.sprint.mission.discodeit.security.init;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {

    if (!userRepository.existsByRole(Role.ADMIN)) {
      log.info("어드민 계정이 존재하지 않아 초기화를 시작합니다.");

      User admin = User.builder()
          .username("admin")
          .email("admin@discodeit.com")
          .password(passwordEncoder.encode("admin1234"))
          .role(Role.ADMIN)
          .build();

      userRepository.save(admin);
      log.info("어드민 계정 생성이 완료되었습니다. (ID: admin)");
    }
  }
}