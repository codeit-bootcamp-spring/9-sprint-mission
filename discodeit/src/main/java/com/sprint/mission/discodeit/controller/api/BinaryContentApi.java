package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "바이너리 콘텐츠 API")
@RequestMapping("/api/binaryContents")
public interface BinaryContentApi {

  @Operation(
      summary = "바이너리 콘텐츠 단건 조회",
      description = "binaryContentId로 바이너리 콘텐츠를 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @io.swagger.v3.oas.annotations.media.Content(
              schema = @Schema(implementation = BinaryContent.class)
          )
      ),
      @ApiResponse(responseCode = "404", description = "콘텐츠를 찾을 수 없음")
  })
  @GetMapping("/{binaryContentId}")
  ResponseEntity<BinaryContent> getBinaryContent(

      @Parameter(
          description = "바이너리 콘텐츠 ID",
          required = true,
//          고유번호로 하드코딩 되는 줄 알았지만 실제 로직과 아무 관계 없음 (@Parameter가 문서 역할만 해줌)
//          request 후 save 하지 않아 저장 X
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @PathVariable UUID binaryContentId
  );


  @Operation(
      summary = "바이너리 콘텐츠 목록 조회",
      description = "여러 개의 binaryContentId로 바이너리 콘텐츠 목록을 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @io.swagger.v3.oas.annotations.media.Content(
              array = @ArraySchema(
                  schema = @Schema(implementation = BinaryContent.class)
              )
          )
      )
  })
  @GetMapping
  ResponseEntity<List<BinaryContent>> getBinaryContents(

      @Parameter(
          description = "바이너리 콘텐츠 ID 목록",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @RequestParam List<UUID> binaryContentIds
  );

  @Operation(
      summary = "바이너리 콘텐츠 다운로드",
      description = "binaryContentId로 파일을 다운로드합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "다운로드 성공"),
      @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음")
  })
  @GetMapping("/{binaryContentId}/download")
  ResponseEntity<?> downloadBinaryContent(

      @Parameter(
          description = "바이너리 콘텐츠 ID",
          required = true,
          example = "123e4567-e89b-12d3-a456-426614174000"
      )
      @PathVariable UUID binaryContentId
  );
}