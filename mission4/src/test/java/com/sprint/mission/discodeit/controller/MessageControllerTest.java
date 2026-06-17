package com.sprint.mission.discodeit.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.exception.Message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @MockBean
  private MessageService messageService;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper mapper;

  @Test
  @DisplayName("이미지 잇는 메시지 생성 -> 201와 함께 created성공")
  void success_createMessage_with_Attachments() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    var request = MessageCreateRequest.builder()
        .content("갱").authorId(userId).channelId(channelId).build();
    byte[] requestContent = mapper.writeValueAsBytes(request);
    MockMultipartFile message = new MockMultipartFile(
        "messageCreateRequest", "", "application/json", requestContent
    );
    MockMultipartFile attachment = new MockMultipartFile(
        "attachments", "test-image.jpg", "image/jpg", new byte[]{1, 2, 3, 4}
    );
    var dto = MessageDto.builder().id(UUID.randomUUID()).content("갱").build();
    given(messageService.create(any(), any())).willReturn(dto);

    mockMvc.perform(multipart("/api/messages")
            .file(message)
            .file(attachment))
        .andExpect(status().isCreated())
        .andDo(print());


  }

  @Test
  @DisplayName("실패: 존재하지 않는 메시지 삭제 시 404 Not Found를 반환한다")
  void delete_Message_Fail_NotFound() throws Exception {
    UUID nonExistId = UUID.randomUUID();

    doThrow(new MessageNotFoundException(nonExistId))
        .when(messageService).delete(nonExistId);

    mockMvc.perform(delete("/api/messages/{messageId}", nonExistId))
        .andExpect(status().isNotFound());
  }
}