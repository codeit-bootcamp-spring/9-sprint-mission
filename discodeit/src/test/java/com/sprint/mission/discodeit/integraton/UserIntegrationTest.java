package com.sprint.mission.discodeit.integraton;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.MatcherAssert.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import static org.assertj.core.api.Assertions.assertThat;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@EnableJpaAuditing
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("사용자 생성 통합 테스트 - 프로필 업로드 포함")
  void createUserWithImage() throws Exception {
    UserCreateRequest request = new UserCreateRequest("test@email.com", "user", "password");

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "image.png",
        MediaType.IMAGE_PNG_VALUE,
        "fake-image-content".getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .file(profilePart)
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("user"))
        .andExpect(jsonPath("$.email").value("test@email.com"));

    Optional<User> savedUser = userRepository.findByUsername("user");
    assertThat(savedUser.isPresent(), is(true));
    assertThat(savedUser.get().getUsername(), is("user"));
    assertThat(savedUser.get().getProfile()).isNotNull();
  }

  @Test
  @DisplayName("사용자 수정 통합 테스트 - 프로필 이미지 교체 포함")
  void updateUserWithImage() throws Exception {
    User user = userRepository.save(new User("test","password" , "test@email.com", null));
    UUID userId = user.getId();

    UserUpdateRequest updateRequest = new UserUpdateRequest("newname", "new@email.com", "newpass");

    MockMultipartFile userUpdatePart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(updateRequest)
    );

    MockMultipartFile newProfilePart = new MockMultipartFile(
        "profile",
        "newprofile.png",
        MediaType.IMAGE_PNG_VALUE,
        "new-image-content".getBytes()
    );

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userUpdatePart)
            .file(newProfilePart)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            })
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("newname"));

    User updatedUser = userRepository.findById(userId).orElseThrow();

    assertThat(updatedUser.getUsername(), is("newname"));
    assertThat(updatedUser.getProfile()).isNotNull();;
  }

  @Test
  @DisplayName("유저 생성 및 로그인 성공/실패")
  void createAndLoginUserScenario() throws Exception {
    UserCreateRequest createRequest = new UserCreateRequest("login@test.com", "login", "password");

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest", "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(createRequest)
    );

    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .with(csrf()))
        .andExpect(status().isCreated());

    // 성공
    LoginRequest loginRequest = new LoginRequest("login", "password");

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("login"));

    // 실패 - 비밀번호 불일치
    LoginRequest wrongPwRequest = new LoginRequest("login", "wrongPassword");

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(wrongPwRequest))
            .with(csrf()))
        .andExpect(status().isUnauthorized());

    // 실패 - 유저 없음
    LoginRequest nonExistRequest = new LoginRequest("ghost", "password");

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nonExistRequest))
            .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

}
