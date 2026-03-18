package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "파일 데이터 관리 API")
public interface BinaryContentApi {

  @Operation(summary = "파일 데이터 단일 조회")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<BinaryContentDto> find(@Parameter(description = "파일 ID") UUID binaryContentId);

  @Operation(summary = "파일 데이터 다중 조회")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @Parameter(description = "파일 ID 리스트") List<UUID> binaryContentIds);
}