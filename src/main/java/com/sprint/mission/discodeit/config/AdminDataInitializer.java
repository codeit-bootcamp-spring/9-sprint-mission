package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = userRepository.findByUsername("admin").orElseGet(() -> User.builder()
                .username("admin")
                .email("admin@discodeit.com")
                .password(passwordEncoder.encode("admin1234!"))
                .build());
        admin.updateRole(Role.ADMIN);
        userRepository.save(admin);
    }
}
