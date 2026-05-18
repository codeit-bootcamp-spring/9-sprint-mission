package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ChannelControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("POST /api/channels/public 성공: 공개 채널 생성 후 201과 Location을 반환한다")
  void createPublic_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    ChannelResponse response = new ChannelResponse(
        channelId,
        ChannelType.PUBLIC,
        "general",
        "전체 공지",
        List.of(new UserResponse(UUID.randomUUID(), "jun", "jun@test.com", null, true)),
        Instant.parse("2026-03-27T00:00:00Z")
    );

    when(channelService.create(any(PublicChannelCreateRequest.class))).thenReturn(response);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PublicChannelCreateRequest("general", "전체 공지"))))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/channels/" + channelId))
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("general"));
  }

  @Test
  @DisplayName("POST /api/channels/private 성공: 현재 사용자와 요청 참여자를 포함해 비공개 채널을 생성한다")
  void createPrivate_success() throws Exception {
    UUID requesterId = UUID.randomUUID();
    UUID participantId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(participantId));
    ChannelResponse response = new ChannelResponse(
        channelId,
        ChannelType.PRIVATE,
        "private",
        null,
        List.of(
            new UserResponse(requesterId, "me", "me@test.com", null, true, UserRole.USER),
            new UserResponse(participantId, "you", "you@test.com", null, false, UserRole.USER)
        ),
        Instant.parse("2026-03-27T00:00:00Z")
    );
    DiscodeitUserDetails principal = new DiscodeitUserDetails(
        new UserResponse(requesterId, "me", "me@test.com", null, true, UserRole.USER),
        "password"
    );

    when(channelService.create(any(PrivateChannelCreateRequest.class), eq(requesterId)))
        .thenReturn(response);

    mockMvc.perform(post("/api/channels/private")
            .with(requestPostProcessor -> {
              SecurityContextHolder.getContext()
                  .setAuthentication(new TestingAuthenticationToken(principal, null, "ROLE_USER"));
              return requestPostProcessor;
            })
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/channels/" + channelId))
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PRIVATE"))
        .andExpect(jsonPath("$.participants.length()").value(2));
  }

  @Test
  @DisplayName("POST /api/channels/public 실패: 요청이 유효하지 않으면 400 에러 JSON을 반환한다")
  void createPublic_fail_validation() throws Exception {
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PublicChannelCreateRequest("", "desc"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_400"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.details.errors").isArray());
  }

  @Test
  @DisplayName("PATCH /api/channels/{id} 실패: 채널이 없으면 404 에러 JSON을 반환한다")
  void update_fail_notFound() throws Exception {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new-name", "new-desc");

    when(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .thenThrow(new ChannelNotFoundException(Map.of("channelId", channelId)));

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_404"))
        .andExpect(jsonPath("$.status").value(404));
  }
}
