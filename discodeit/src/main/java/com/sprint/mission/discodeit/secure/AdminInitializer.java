package com.sprint.mission.discodeit.secure;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
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

  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;


  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("ADMIN 계정 초기화 확인");
    if (userRepository.existsByRole(UserRole.ADMIN)) {
      log.info("ADMIN 계정이 이미 존재합니다.");
      return;
    }
    User admin = new User("admin", "admin@discodeit.com",
        passwordEncoder.encode("admin1234"), null);
    admin.updateRole(UserRole.ADMIN);
    userRepository.save(admin);
    log.info("ADMIN 계정이 생성되었습니다.");
  }
}
