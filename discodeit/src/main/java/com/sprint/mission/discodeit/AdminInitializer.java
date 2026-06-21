package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    if (!userRepository.existsByUsername("admin")) {
      try {
        String password = passwordEncoder.encode("password1234");
        User admin = new User("admin", "admin@service.com", password, null);
        admin.upateRole(Role.ADMIN);
        userRepository.save(admin);
        log.info("Successfully created default admin user.");
      } catch (Exception e) {
        log.warn("Failed to initialize admin user (it might have been created by another instance concurrently): {}", e.getMessage());
      }
    }
  }
}
