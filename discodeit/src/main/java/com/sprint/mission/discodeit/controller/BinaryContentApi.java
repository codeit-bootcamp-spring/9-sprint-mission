package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
public interface BinaryContentApi {

  @Operation(summary = "여러 첨부 파일 조회")
  @GetMapping
  ResponseEntity<List<BinaryContentDto>> findAllByIdIn(@RequestParam List<UUID> binaryContentIds);

  @Operation(summary = "첨부 파일 조회")
  @GetMapping("/{binaryContentId}")
  ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId);

  @Operation(summary = "파일 다운로드")
  @GetMapping("/{binaryContentId}/download")
  ResponseEntity<Resource> download(@PathVariable UUID binaryContentId);
}