package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = ChannelController.class, excludeAutoConfiguration = JpaConfig.class)
@ActiveProfiles("test")
@DisplayName("ChannelController 슬라이스 테스트")
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  private ChannelDto publicChannelDto;
  private UUID channelId;

  @BeforeEach
  void setUp() {
    channelId = UUID.randomUUID();
    publicChannelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "공개 채널", "설명", List.of(),
        null);
  }

  @Test
  @DisplayName("Public 채널 생성 - 성공")
  void createPublicChannel_Success() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개 채널", "설명");
    given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(
        publicChannelDto);

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("공개 채널"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("Private 채널 생성 - 성공")
  void createPrivateChannel_Success() throws Exception {
    // given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    UserDto userDto1 = new UserDto(userId1, "이름1", "mail1@g.com", null, null);
    UserDto userDto2 = new UserDto(userId2, "이름2", "mail2@g.com", null, null);
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of(userId1, userId2));
    ChannelDto privateDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "비밀 채널", "설명",
        List.of(userDto1, userDto2), null);
    given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(privateDto);

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  @DisplayName("채널 수정 - 성공")
  void updateChannel_Success() throws Exception {
    // given
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수정된 이름", "수정된 설명");
    ChannelDto updatedDto = new ChannelDto(channelId, ChannelType.PUBLIC, "수정된 이름", "수정된 설명",
        List.of(), null);
    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(
        updatedDto);

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("수정된 이름"));
  }

  @Test
  @DisplayName("사용자 ID로 채널 목록 조회 - 성공")
  void findAllByUserId_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    given(channelService.findByUserId(userId)).willReturn(List.of(publicChannelDto));

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("공개 채널"));
  }

  @Test
  @DisplayName("채널 삭제 - 성공 (204 No Content)")
  void deleteChannel_Success() throws Exception {
    // given
    willDoNothing().given(channelService).delete(channelId);

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }
}