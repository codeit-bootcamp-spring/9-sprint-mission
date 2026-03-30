package com.sprint.mission.discodeit.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper mapper;
  @MockBean
  private UserService userService;
  @MockBean
  private UserStatusService userStatusService;

  @Test
  @DisplayName("프로필 없는 유저 생성 시 201와 함께 생성된 유저 정보 반환")
  void create_User_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    var request = UserCreateRequest.builder().username("seunghyeon").email("seung@naver.com")
        .password("124245243634").build();
    byte[] content = mapper.writeValueAsBytes(request);
    UserDto responseDto = UserDto.builder().id(userId).username("seunghyeon")
        .email("seung@naver.com").build();
    given(userService.create(any(), any())).willReturn(responseDto);
    mockMvc.perform(multipart("/api/users")
            .file(new MockMultipartFile("userCreateRequest", "", "application/json", content))
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("seunghyeon"));

  }

  @Test
  @DisplayName("실패: 존재하지 않는 유저 삭제 시 404 Not Found를 반환한다")
  void delete_User_Fail_NotFound() throws Exception {
    UUID nonExistId = UUID.randomUUID();

    doThrow(new UserNotFoundException("유저를 찾을 수 없습니다."))
        .when(userService).delete(nonExistId);

    mockMvc.perform(delete("/api/users/{userId}", nonExistId))
        .andExpect(status().isNotFound());
  }
}