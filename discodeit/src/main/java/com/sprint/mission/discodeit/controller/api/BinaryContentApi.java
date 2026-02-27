package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "BinaryContent API")
@RequestMapping("/api/binary-contents")
public interface BinaryContentApi {

  @Operation(summary = "바이너리 파일 다운로드")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "다운로드 성공"),
      @ApiResponse(responseCode = "404", description = "Not Found")
  })
  @GetMapping("/{binaryContentId}/download")
  ResponseEntity<byte[]> download(
      @Parameter(description = "BinaryContent ID")
      @PathVariable UUID binaryContentId
  );

  @Operation(summary = "BinaryContent 단건 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContentView.class))
      ),
      @ApiResponse(responseCode = "404", description = "Not Found")
  })
  @GetMapping("/{binaryContentId}")
  BinaryContentView findById(
      @Parameter(description = "BinaryContent ID")
      @PathVariable UUID binaryContentId
  );

  @Operation(summary = "BinaryContent 여러 개 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContentView.class))
      )
  })
  @GetMapping
  List<BinaryContentView> findAllByIdIn(
      @Parameter(description = "BinaryContent ID 목록")
      @RequestParam("ids") List<UUID> ids
  );
}
