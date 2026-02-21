package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "BinaryContent", description = "BinaryContent API")
public interface BinaryContentApi {

  @Operation(summary = "BinaryContent 전체 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "전체 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class)))
      )
  })
  ResponseEntity<List<BinaryContent>> findAllByIds(
      @Parameter(description = "조회할 파일 ID 목록") List<UUID> ids
  );

  @Operation(summary = "BinaryContent 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "BinaryContent 조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContent.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "없는 ID입니다",
          content = @Content(examples = @ExampleObject("{ids} not found"))
      )
  })
  ResponseEntity<BinaryContent> find(
      @Parameter(description = "조회할 Id") UUID id
  );
}
