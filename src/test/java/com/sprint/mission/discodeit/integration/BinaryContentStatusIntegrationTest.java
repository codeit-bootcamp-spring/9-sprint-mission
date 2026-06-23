package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("바이너리 컨텐츠 상태 관리 통합 테스트")
class BinaryContentStatusIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BinaryContentService binaryContentService;

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("바이너리 컨텐츠 생성 시 초기 상태가 PROCESSING")
  void createBinaryContent_InitialStatusIsProcessing() throws Exception {
    // Given
    String fileContent = "테스트 파일 내용";
    BinaryContentCreateRequest createRequest = new BinaryContentCreateRequest(
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        fileContent.getBytes()
    );

    // When
    BinaryContentDto result = binaryContentService.create(createRequest);

    // Then
    assertThat(result.status()).isEqualTo(BinaryContentStatus.PROCESSING);
    
    // Verify via API
    mockMvc.perform(get("/api/binaryContents/{id}", result.id())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PROCESSING"));
  }

  @Test
  @WithMockUser(roles = "USER")
  @DisplayName("다중 바이너리 컨텐츠 생성 시 모두 초기 상태")
  void createMultipleBinaryContents_AllHaveInitialStatus() throws Exception {
    // Given
    BinaryContentDto content1 = binaryContentService.create(
        new BinaryContentCreateRequest("file1.txt", "text/plain", "content1".getBytes())
    );
    BinaryContentDto content2 = binaryContentService.create(
        new BinaryContentCreateRequest("file2.txt", "text/plain", "content2".getBytes())
    );

    // When & Then - Both should have PROCESSING status
    mockMvc.perform(get("/api/binaryContents")
            .param("binaryContentIds", content1.id().toString(), content2.id().toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status").value("PROCESSING"))
        .andExpect(jsonPath("$[1].status").value("PROCESSING"));
  }
}