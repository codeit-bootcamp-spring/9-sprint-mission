package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
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
    @DisplayName("POST /api/users - 성공: 사용자를 생성한다")
    void create_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(userId, "testuser", "test@email.com", null, true);
        given(userService.create(any(), any(Optional.class))).willReturn(userDto);

        UserCreateRequest request = new UserCreateRequest("testuser", "test@email.com",
            "password1234");
        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));

        // when & then
        mockMvc.perform(multipart("/api/users").file(requestPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@email.com"));
    }

    @Test
    @DisplayName("GET /api/users - 성공: 사용자 목록을 조회한다")
    void findAll_success() throws Exception {
        // given
        UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@email.com", null, true);
        given(userService.findAll()).willReturn(List.of(userDto));

        // when & then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - 성공: 사용자를 삭제한다")
    void delete_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        willDoNothing().given(userService).delete(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - 실패: 존재하지 않는 사용자")
    void delete_fail_notFound() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        willThrow(new UserNotFoundException(Map.of("userId", userId)))
            .given(userService).delete(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    @DisplayName("PATCH /api/users/{userId} - 성공: 사용자 정보를 수정한다")
    void update_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(userId, "newuser", "new@email.com", null, true);
        given(userService.update(any(UUID.class), any(), any(Optional.class))).willReturn(userDto);

        UserUpdateRequest request = new UserUpdateRequest("newuser", "new@email.com", null);
        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(requestPart)
                .with(req -> { req.setMethod("PATCH"); return req; }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    @DisplayName("PATCH /api/users/{userId}/userStatus - 성공: 사용자 상태를 갱신한다")
    void updateUserStatus_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        UserStatusDto statusDto = new UserStatusDto(UUID.randomUUID(), userId, now);
        given(userStatusService.updateByUserId(any(UUID.class), any(UserStatusUpdateRequest.class)))
            .willReturn(statusDto);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserStatusUpdateRequest(now))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId.toString()));
    }
}
