package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.integration.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class UserIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("사용자 생성 API를 호출하면 실제로 DB에 유저가 저장되어야한다")
  void createdUser_Integration_Success() throws Exception {
    var request = UserCreateRequest.builder()
        .username("갱").email("갱@naver.com").password("5132312351").build();
    MockMultipartFile file = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        mapper.writeValueAsBytes(request)
    );
    MockMultipartFile profile = new MockMultipartFile(
        "profile",
        "test.png",
        MediaType.IMAGE_PNG_VALUE,
        new byte[]{1, 2, 3}
    );

    mockMvc.perform(multipart("/api/users")
            .file(file)
            .file(profile))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("갱"));

    var savedUser = userRepository.findByUsername("갱").orElseThrow();
    assertThat(savedUser.getEmail()).isEqualTo("갱@naver.com");
  }

  @Test
  @DisplayName("사용자 수정 API를 호출하면 실제 DB에서도 유저가 수정되어야한다(프로필x)")
  void updatedUser_Success() throws Exception {
    User user = userRepository.save(
        User.builder().username("갱").email("갱@naver.com").password("1242134324").build());
    var newUser = UserUpdateRequest.builder().newUsername("승").newEmail("승@naver.com")
        .newPassword("1231234554").build();
    var file = new MockMultipartFile(
        "userUpdateRequest",
        "",
        "application/json",
        mapper.writeValueAsBytes(newUser)
    );
    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/" + user.getId())
            .file(file))
        .andExpect(status().isOk());

    var updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getUsername()).isEqualTo("승");
    assertThat(updatedUser.getEmail()).isEqualTo("승@naver.com");

  }
}
