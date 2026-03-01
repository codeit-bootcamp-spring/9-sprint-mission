package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
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

@Tag(name = "ReadStatus", description = "ReadStatus API")
public interface ReadStatusApi {

  @Operation(summary = "읽음 상태 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "읽음 상태 생성 성공",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))
      )
  })
  ResponseEntity<ReadStatus> create(
      @Parameter(description = "읽음 상태 생성 정보") ReadStatusCreateRequest request
  );

  @Operation(summary = "읽은 상태 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "읽음 상태 수정 성공",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "읽음 정보 없음",
          content = @Content(examples = @ExampleObject("User with id {userId} not found"))
      )
  })
  ResponseEntity<ReadStatus> update(
      @Parameter(description = "수정할 ReadStatus ID") UUID readStatusId,
      @Parameter(description = "수정할 ReadStatus 정보") ReadStatusUpdateRequest request
  );

  @Operation(summary = "읽은 상태 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "읽음 상태 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class)))
      )
  })
  ResponseEntity<List<ReadStatus>> findAllByUserId(
      @Parameter(description = "읽은 상태를 조회할 UserID") UUID userId
  );
}
