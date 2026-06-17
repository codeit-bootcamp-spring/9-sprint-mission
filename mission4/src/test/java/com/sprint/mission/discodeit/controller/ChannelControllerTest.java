package com.sprint.mission.discodeit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper mapper;
  @MockBean
  private ChannelService channelService;

  @Test
  @DisplayName("공용 채널 201과 함께 생성 성공")
  void create_Success_PublicChannel() throws Exception {
    var request = PublicChannelCreateRequest.builder()
        .name("갱").description("갱갱").build();
    UUID id = UUID.randomUUID();
    ChannelDto publicChannel = ChannelDto.builder().id(id).name("갱").description("갱갱").build();
    given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(publicChannel);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andExpect(status().isCreated()
        )
        .andExpect(jsonPath("$.name").value("갱"))
        .andDo(print());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 채널 삭제 시 404 Not Found를 반환한다")
  void delete_Channel_Fail_NotFound() throws Exception {
    UUID nonExistId = UUID.randomUUID();

    doThrow(new ChannelNotFoundException(nonExistId))
        .when(channelService).delete(nonExistId);

    mockMvc.perform(delete("/api/channels/{userId}", nonExistId))
        .andExpect(status().isNotFound());
  }


}