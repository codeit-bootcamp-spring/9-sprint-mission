package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
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
    boolean adminExists = userRepository.findAll()
        .stream()
        .anyMatch(user -> user.getRole() == Role.ADMIN);

    if (!adminExists) {
      String encodedPassword = passwordEncoder.encode("admin1234");
      User admin = new User("admin", "admin@discodeit.com", encodedPassword, null);
      admin.updateRole(Role.ADMIN);
      userRepository.save(admin);
      log.info("ADMIN 계정이 초기화되었습니다. username: admin");
    }
  }

}
