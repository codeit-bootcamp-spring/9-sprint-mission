package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChannelApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @Transactional
  @DisplayName("POST /api/channels/public 성공: 공개 채널을 생성하고 Location을 반환한다")
  void createPublic_success() throws Exception {
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(new PublicChannelCreateRequest("general", "전체 공지"))))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("general"));
  }

  @Test
  @Transactional
  @DisplayName("PATCH /api/channels/{id} 성공: 공개 채널 이름과 설명을 수정한다")
  void update_success() throws Exception {
    UUID channelId = createPublicChannel("general", "전체 공지");

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(new PublicChannelUpdateRequest("notice", "업데이트 공지"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value("notice"));
  }

  @Test
  @Transactional
  @DisplayName("DELETE /api/channels/{id} 성공: 삭제 후 다시 수정하면 404를 반환한다")
  void delete_success() throws Exception {
    UUID channelId = createPublicChannel("general", "전체 공지");

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new PublicChannelUpdateRequest("after-delete", "fail"))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_404"));
  }

  @Test
  @Transactional
  @DisplayName("PATCH /api/channels/{id} 실패: 없는 채널을 수정하면 404를 반환한다")
  void update_fail_notFound() throws Exception {
    mockMvc.perform(patch("/api/channels/{channelId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PublicChannelUpdateRequest("notice", "desc"))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL_404"))
        .andExpect(jsonPath("$.status").value(404));
  }

  private UUID createPublicChannel(String name, String description) throws Exception {
    MvcResult result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(new PublicChannelCreateRequest(name, description))))
        .andExpect(status().isCreated())
        .andReturn();

    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    return UUID.fromString(body.get("id").asText());
  }
}
