package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto; // DTO 임포트
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
public interface BinaryContentApi {

  // 실제 파일 다운로드
  @Operation(summary = "첨부 파일 다운로드")
  @ApiResponse(responseCode = "200", description = "파일 다운로드 성공")
  ResponseEntity<?> download(UUID id);

  // 단건 메타데이터 조회
  @Operation(summary = "첨부 파일 메타데이터 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "메타데이터 조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContentDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "파일을 찾을 수 없음"
      )
  })
  ResponseEntity<BinaryContentDto> findMetadata(UUID id);

  // 3. 목록 메타데이터 조회
  @Operation(summary = "여러 첨부 파일 메타데이터 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "메타데이터 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentDto.class)))
      )
  })
  ResponseEntity<List<BinaryContentDto>> findAllMetadata(List<UUID> ids);
}