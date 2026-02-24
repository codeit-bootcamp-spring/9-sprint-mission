package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
public interface BinaryContentApi {

  @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn")
  @GetMapping("/api/binaryContents")
  ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds);

  @Operation(summary = "첨부 파일 조회", operationId = "find")
  @GetMapping("/api/binaryContents/{binaryContentId}")
  ResponseEntity<BinaryContentResponse> find(@PathVariable UUID binaryContentId);
}