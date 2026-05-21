package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

  private final AdminProperties adminProperties;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(Role.ADMIN)) {
      return;
    }
    userRepository.findByUsername(adminProperties.username())
        .ifPresentOrElse(
            user -> user.updateRole(Role.ADMIN),
            this::createAdmin
        );
  }

  private void createAdmin() {
    User admin = new User(
        adminProperties.username(),
        adminProperties.email(),
        passwordEncoder.encode(adminProperties.password()),
        null,
        Role.ADMIN
    );
    userRepository.save(admin);
  }

  @ConfigurationProperties(prefix = "discodeit.admin")
  public record AdminProperties(
      String username,
      String email,
      String password
  ) {

  }
}
