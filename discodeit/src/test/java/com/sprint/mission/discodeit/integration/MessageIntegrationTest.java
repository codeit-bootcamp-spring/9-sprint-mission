package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("메시지 생성 - 실패 (필수값 누락 Validation 확인)")
  void createMessage_Fail_Validation() throws Exception {
    MessageCreateRequest badRequest = new MessageCreateRequest("", UUID.randomUUID(), UUID.randomUUID());

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(badRequest)
    );

    // When & Then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("채널 메시지 목록 조회 - 실패 (존재하지 않는 채널)")
  void findAllMessages_Fail() throws Exception {
    // Given
    UUID fakeChannelId = UUID.randomUUID();

    // When & Then
    mockMvc.perform(get("/api/messages")
            .param("channelId", fakeChannelId.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()); // 채널이 없어서 터지는 에러 상태코드에 맞게 (404 등) 수정
  }
}