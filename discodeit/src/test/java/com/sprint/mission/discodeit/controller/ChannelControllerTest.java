package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ChannelService channelService;

  @Test
  @DisplayName("퍼블릭 채널 생성 성공 (HTTP 201)")
  void createPublicChannel_Success() throws Exception {
    String requestJson = "{\"name\":\"공지사항채널\", \"description\":\"공지 채널입니다.\"}";

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("퍼블릭 채널 생성 실패 - 유효하지 않은 요청(빈 객체) (HTTP 400)")
  void createPublicChannel_Fail_Validation() throws Exception {
    String invalidJson = "{}";

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("프라이빗 채널 생성 성공 (HTTP 201)")
  void createPrivateChannel_Success() throws Exception {
    String requestJson = "{\"participantIds\":[\"" + UUID.randomUUID() + "\", \"" + UUID.randomUUID() + "\"]}";

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("프라이빗 채널 생성 실패 - 유효하지 않은 요청 (HTTP 400)")
  void createPrivateChannel_Fail_Validation() throws Exception {
    String invalidJson = "{}";

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널 수정 성공 (HTTP 200)")
  void updateChannel_Success() throws Exception {
    UUID channelId = UUID.randomUUID();
    String requestJson = "{\"newName\":\"수정된공지채널\", \"newDescription\":\"수정된 설명\"}";

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("채널 수정 실패 - 유효하지 않은 요청 (HTTP 400)")
  void updateChannel_Fail_Validation() throws Exception {
    UUID channelId = UUID.randomUUID();
    String invalidJson = "{}";

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널 삭제 성공 (HTTP 204)")
  void deleteChannel_Success() throws Exception {
    UUID channelId = UUID.randomUUID();

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("유저 ID로 채널 목록 조회 성공 (JSON 배열 응답 검증)")
  void findAllByUserId_Success() throws Exception {
    UUID userId = UUID.randomUUID();

    given(channelService.findAllByUserId(userId)).willReturn(Collections.emptyList());

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("유저 ID로 채널 목록 조회 실패 - 파라미터 타입 불일치 (HTTP 400)")
  void findAllByUserId_Fail_TypeMismatch() throws Exception {
    mockMvc.perform(get("/api/channels")
            .param("userId", "not-a-uuid"))
        .andDo(print())
        .andExpect(status().isInternalServerError());
  }
}