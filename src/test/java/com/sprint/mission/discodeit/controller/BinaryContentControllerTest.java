package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BinaryContentController.class)
class BinaryContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BinaryContentService binaryContentService;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("GET /api/binaryContents/{id} - 성공: 파일 메타데이터를 조회한다")
    void find_success() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContentDto dto = new BinaryContentDto(contentId, "test.png", 1024L, "image/png");
        given(binaryContentService.find(contentId)).willReturn(dto);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}", contentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileName").value("test.png"))
            .andExpect(jsonPath("$.contentType").value("image/png"));
    }

    @Test
    @DisplayName("GET /api/binaryContents/{id} - 실패: 파일 미존재")
    void find_fail_notFound() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();
        willThrow(new BinaryContentNotFoundException(Map.of("binaryContentId", contentId)))
            .given(binaryContentService).find(contentId);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}", contentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("BINARY_CONTENT_NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/binaryContents - 성공: ID 목록으로 파일 메타데이터를 조회한다")
    void findAllByIdIn_success() throws Exception {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        BinaryContentDto dto1 = new BinaryContentDto(id1, "a.png", 100L, "image/png");
        BinaryContentDto dto2 = new BinaryContentDto(id2, "b.pdf", 200L, "application/pdf");
        given(binaryContentService.findAllByIdIn(List.of(id1, id2))).willReturn(List.of(dto1, dto2));

        // when & then
        mockMvc.perform(get("/api/binaryContents")
                .param("binaryContentIds", id1.toString(), id2.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fileName").value("a.png"))
            .andExpect(jsonPath("$[1].fileName").value("b.pdf"));
    }

    @Test
    @DisplayName("GET /api/binaryContents/{id}/download - 성공: 파일을 다운로드한다")
    void download_success() throws Exception {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContentDto dto = new BinaryContentDto(contentId, "test.txt", 11L, "text/plain");
        given(binaryContentService.find(contentId)).willReturn(dto);

        ResponseEntity<?> downloadResponse = ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.txt\"")
            .contentType(MediaType.TEXT_PLAIN)
            .contentLength(11L)
            .body(new ByteArrayResource("hello world".getBytes()));
        given(binaryContentStorage.download(dto)).willReturn((ResponseEntity) downloadResponse);

        // when & then
        mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", contentId))
            .andExpect(status().isOk());
    }
}
