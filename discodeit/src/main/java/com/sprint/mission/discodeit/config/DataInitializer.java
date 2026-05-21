package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public void run(String... args) {
    if (!userRepository.existsByRole(Role.ADMIN)) {

      User admin = new User(
          "admin",
          "admin@gmail.com",
          "admin1234",
          null
      );

      admin.updateRole(Role.ADMIN);
      userRepository.save(admin);

      System.out.println("초기 어드민 계정이 생성되었습니다: admin@gmail.com");
    } else {
      System.out.println("이미 어드민 계정이 존재하여 초기화 로직을 건너뜁니다.");
    }
  }
}
