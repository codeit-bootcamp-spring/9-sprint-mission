package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(prefix = "discodeit.admin", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AdminAccountInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${discodeit.admin.username:admin}")
  private String adminUsername;

  @Value("${discodeit.admin.email:admin@discodeit.local}")
  private String adminEmail;

  @Value("${discodeit.admin.password:admin1234}")
  private String adminPassword;

  @Transactional
  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(UserRole.ADMIN)) {
      log.debug("Admin account initialization skipped: ADMIN already exists");
      return;
    }

    User admin = userRepository.findByUsername(adminUsername)
        .map(this::promoteToAdmin)
        .orElseGet(this::createAdmin);

    log.info("Admin account initialized: userId={}, username={}",
        admin.getId(), admin.getUsername());
  }

  private User promoteToAdmin(User user) {
    user.updateRole(UserRole.ADMIN);
    return user;
  }

  private User createAdmin() {
    User admin = new User(
        adminUsername,
        adminEmail,
        passwordEncoder.encode(adminPassword),
        UserRole.ADMIN,
        null
    );
    return userRepository.save(admin);
  }
}
