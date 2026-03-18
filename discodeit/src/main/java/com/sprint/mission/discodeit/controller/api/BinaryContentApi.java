package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentApi {

  @Operation(summary = "BinaryContent 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "BinaryContent 조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContent.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "BinaryContent를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = " BinaryContent with id {binaryContentId} not found"))
      )
  })
  public ResponseEntity<BinaryContentDto> find(@Parameter(description = "조회 할 BinaryContent ID") UUID binaryContentId);

  @Operation(summary = "BinaryContent 다건 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "BinaryContent 다건 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class)))
      )
  })
  public ResponseEntity<List<BinaryContentDto>> findByIds(@Parameter(description = "조회 할 BinaryContent ID 목록") List<UUID> idList);
}
