package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = MessageController.class, excludeAutoConfiguration = JpaConfig.class)
@ActiveProfiles("test")
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  private MessageDto messageDto;
  private UUID messageId;
  private UUID channelId;

  @BeforeEach
  void setUp() {
    messageId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    messageDto = new MessageDto(messageId, Instant.now(), null, "테스트 메시지", channelId, null,
        List.of());
  }

  @Test
  @DisplayName("메시지 생성 - 성공 (Multipart 요청)")
  void createMessage_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("테스트 메시지", channelId, userId);

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    given(messageService.create(any(MessageCreateRequest.class), any())).willReturn(messageDto);

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("테스트 메시지"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()));
  }

  @Test
  @DisplayName("메시지 수정 - 성공")
  void updateMessage_Success() throws Exception {
    // given
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");
    MessageDto updatedDto = new MessageDto(messageId, Instant.now(), Instant.now(), "수정된 메시지",
        channelId, null, List.of());

    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class))).willReturn(
        updatedDto);

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("수정된 메시지"));
  }

  @Test
  @DisplayName("채널 내 메시지 목록 조회 - 성공 (PageResponse 모킹 적용)")
  void findAllByChannelId_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    MessageDto messageDto = new MessageDto(
        UUID.randomUUID(), Instant.now(), null, "테스트 내용", channelId, null, List.of()
    );

    // 1. PageResponse 모킹
    @SuppressWarnings("unchecked")
    PageResponse<MessageDto> mockResponse = mock(PageResponse.class);

    // 2. 제공해주신 필드명(content, hasNext 등)의 Getter 스터빙
    given(mockResponse.getContent()).willReturn(List.of(messageDto)); // data 대신 content
    given(mockResponse.isHasNext()).willReturn(false);
    given(mockResponse.getTotalElements()).willReturn(1L); // totalCount 대신 totalElements

    given(messageService.findAllByChannelId(eq(channelId), any(), any()))
        .willReturn(mockResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        // 3. JSON 경로도 제공해주신 필드명인 'content'로 검증
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].content").value("테스트 내용"))
        .andExpect(jsonPath("$.hasNext").value(false));
  }
}