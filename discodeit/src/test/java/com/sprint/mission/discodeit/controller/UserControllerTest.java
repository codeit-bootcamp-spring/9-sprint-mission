package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart; // multipart 사용
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @Test
  @DisplayName("유저 생성 API 성공 테스트 - Multipart 요청")
  void create_user_api_success() throws Exception {
    // Given
    UserDto mockUserDto = org.mockito.Mockito.mock(UserDto.class);
    given(mockUserDto.username()).willReturn("tester");

    // 서비스의 create 메서드가 인자 2개를 받으므로 똑같이 설정합니다.
    given(userService.create(any(UserCreateRequest.class), any(Optional.class)))
        .willReturn(mockUserDto);

    // When & Then
    // 컨트롤러가 MULTIPART_FORM_DATA를 원하므로 multipart() 요청을 보냅니다.
    mockMvc.perform(multipart("/api/users")
            .file("username", "tester".getBytes())
            .file("email", "test@test.com".getBytes())
            .file("password", "pass123".getBytes())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("tester"));
  }
}