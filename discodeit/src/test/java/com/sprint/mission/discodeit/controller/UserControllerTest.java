package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class})
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @Test
  @DisplayName("전체 사용자 조회 - 성공 (JSON 응답 검증)")
  void findAll_Success() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();

    UserDto mockUser = new UserDto(userId, "tester", "test@email.com", null, true);

    given(userService.findAll()).willReturn(List.of(mockUser));

    // When & Then
    mockMvc.perform(get("/api/users")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // HTTP 200 검증
        .andExpect(jsonPath("$.length()").value(1)) // 리스트 길이 검증
        .andExpect(jsonPath("$[0].username").value("tester")) // 첫 번째 객체의 필드 검증
        .andExpect(jsonPath("$[0].online").value(true));
  }

  @Test
  @DisplayName("사용자 삭제 - 실패 (존재하지 않는 사용자 삭제 시도)")
  void delete_Fail_NotFound() throws Exception {
    // Given
    UUID fakeUserId = UUID.randomUUID();

    willThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."))
        .given(userService).delete(fakeUserId);

    // When & Then
    mockMvc.perform(delete("/api/users/{userId}", fakeUserId))
        .andExpect(status().isBadRequest());
  }
}