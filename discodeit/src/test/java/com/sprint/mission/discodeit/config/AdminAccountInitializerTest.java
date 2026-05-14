package com.sprint.mission.discodeit.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminAccountInitializerTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  private AdminAccountInitializer initializer;

  @BeforeEach
  void setUp() {
    initializer = new AdminAccountInitializer(userRepository, passwordEncoder);
    ReflectionTestUtils.setField(initializer, "adminUsername", "admin");
    ReflectionTestUtils.setField(initializer, "adminEmail", "admin@test.com");
    ReflectionTestUtils.setField(initializer, "adminPassword", "password123");
  }

  @Test
  @DisplayName("run 성공: ADMIN 권한 사용자가 있으면 초기화를 건너뛴다")
  void run_success_skipWhenAdminExists() {
    given(userRepository.existsByRole(UserRole.ADMIN)).willReturn(true);

    initializer.run(null);

    then(userRepository).should().existsByRole(UserRole.ADMIN);
    then(userRepository).shouldHaveNoMoreInteractions();
    then(passwordEncoder).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("run 성공: ADMIN이 없으면 어드민 계정을 생성한다")
  void run_success_createAdmin() {
    given(userRepository.existsByRole(UserRole.ADMIN)).willReturn(false);
    given(userRepository.findByUsername("admin")).willReturn(Optional.empty());
    given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
    given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

    initializer.run(null);

    then(userRepository).should().save(any(User.class));
    then(passwordEncoder).should().encode("password123");
  }

  @Test
  @DisplayName("run 성공: 기본 username 계정이 있으면 ADMIN으로 승격한다")
  void run_success_promoteExistingUser() {
    User user = new User("admin", "admin@test.com", "encodedPassword", null);

    given(userRepository.existsByRole(UserRole.ADMIN)).willReturn(false);
    given(userRepository.findByUsername("admin")).willReturn(Optional.of(user));

    initializer.run(null);

    assertEquals(UserRole.ADMIN, user.getRole());
    then(userRepository).should().findByUsername("admin");
    then(userRepository).shouldHaveNoMoreInteractions();
    then(passwordEncoder).shouldHaveNoInteractions();
  }
}
