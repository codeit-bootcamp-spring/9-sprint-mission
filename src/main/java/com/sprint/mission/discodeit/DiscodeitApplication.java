package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscodeitApplication {

  public static void main(String[] args) {
    SpringApplication.run(DiscodeitApplication.class, args);
  }

  @Bean
  public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      boolean exists = userRepository.existsByRole(Role.ADMIN);
      if (exists) return;
      User admin = new User(
          "admin",
          "admin@admin.com",
          passwordEncoder.encode("admin123"),
          null,
          Role.ADMIN
      );
      userRepository.save(admin);
    };
  }
}
