package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto; // 💡 DTO 임포트
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일(바이너리) 조회 API")
public interface BinaryContentApi {

  @Operation(summary = "첨부 파일 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "첨부 파일 조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContentDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "첨부 파일을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })

  ResponseEntity<BinaryContentDto> find(
      @Parameter(description = "조회할 파일 ID") @RequestParam("binaryContentId") UUID binaryContentId
  );

  @Operation(summary = "첨부 파일 다건 조회 (ID 목록)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "첨부 파일 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentDto.class)))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 데이터",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })

  ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @Parameter(description = "조회할 파일 ID 목록") @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  );
}