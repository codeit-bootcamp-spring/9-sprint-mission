package com.sprint.mission.discodeit.slice;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.sprint.mission.discodeit.controller.MessageController;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@WebMvcTest(controllers = MessageController.class)
public class MessageControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MessageService messageService;

  @MockitoBean
  private BinaryContentService binaryContentService;

  private UUID channelId;

  @BeforeEach
  void setUp() {
    channelId = UUID.randomUUID();
  }

  @Test
  @DisplayName("특정 채널의 메시지 조회 성공")
  void findByChannelId_Success() throws Exception {

    UserDto authorDto = new UserDto(UUID.randomUUID(), "user", "user@test.com", null,  true);
    MessageDto msg1 = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "first", channelId, authorDto, null);
    MessageDto msg2 = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "seconde", channelId, authorDto, null);
    //MessageDto msg3 = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "third", UUID.randomUUID(), authorDto, null);

    PageResponse<MessageDto> response = new PageResponse<>(List.of(msg1, msg2),
        Instant.now().toString(),
        2,
        true,
        3L);

    given(messageService.findAllByChannelId(eq(channelId), eq(null), any(Pageable.class)))
        .willReturn(response);

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("size", "2")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.nextCursor").exists());
  }

  @Test
  @DisplayName("특정 채널이 존재하지 않아 메시지 조회 실패 (빈 결과 반환)")
  void findByChannelId_fail() throws Exception {
    PageResponse<MessageDto> response = new PageResponse<>(
        Collections.emptyList(),
        null,
        2,
        false,
        0L
    );

    given(messageService.findAllByChannelId(eq(channelId), any(), any(Pageable.class)))
        .willReturn(response);

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("size", "2")
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.nextCursor").doesNotExist());
  }
}
