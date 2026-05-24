package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.service.MessageService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private MessageService messageService;

  @Test
  @DisplayName("메시지 생성 성공 (첨부파일 포함, HTTP 201)")
  void createMessage_Success() throws Exception {

    String requestJson = "{\"channelId\":\"" + UUID.randomUUID() + "\", \"authorId\":\"" + UUID.randomUUID() + "\", \"content\":\"안녕하세요! 반가워요!\"}";

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes()
    );

    MockMultipartFile attachment1 = new MockMultipartFile(
        "attachments", "file1.txt", MediaType.TEXT_PLAIN_VALUE, "Hello file 1".getBytes()
    );
    MockMultipartFile attachment2 = new MockMultipartFile(
        "attachments", "image.png", MediaType.IMAGE_PNG_VALUE, "dummy image".getBytes()
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest)
            .file(attachment1)
            .file(attachment2)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("메시지 생성 실패 - 유효성 검사 실패(빈 객체) (HTTP 500)")
  void createMessage_Fail_Validation() throws Exception {
    String invalidJson = "{}";
    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, invalidJson.getBytes()
    );

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andDo(print())
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("메시지 수정 성공 (HTTP 200)")
  void updateMessage_Success() throws Exception {
    UUID messageId = UUID.randomUUID();
    String requestJson = "{\"newContent\":\"수정된 메시지입니다.\"}";

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("메시지 수정 실패 - 유효성 검사 실패(빈 객체) (HTTP 400)")
  void updateMessage_Fail_Validation() throws Exception {
    UUID messageId = UUID.randomUUID();
    String invalidJson = "{}";

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("메시지 삭제 성공 (HTTP 204)")
  void deleteMessage_Success() throws Exception {
    UUID messageId = UUID.randomUUID();

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 내 메시지 조회 성공 (페이징, HTTP 200)")
  void findAllByChannelId_Success() throws Exception {
    UUID channelId = UUID.randomUUID();

    given(messageService.findAllByChannelId(any(), any(), any())).willReturn(null);

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("size", "20")
            .param("sort", "createdAt,desc"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("채널 내 메시지 조회 실패 - 필수 파라미터(channelId) 누락 (HTTP 500)")
  void findAllByChannelId_Fail_MissingParameter() throws Exception {
    mockMvc.perform(get("/api/messages"))
        .andDo(print())
        .andExpect(status().isInternalServerError());
  }
}