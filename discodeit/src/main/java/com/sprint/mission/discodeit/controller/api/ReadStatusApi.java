package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.readStatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readStatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface ReadStatusApi {
  @Operation(summary = "ReadStatus 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "ReadStatus가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))
      ),
  })
  public ResponseEntity<ReadStatus> create(@Parameter(
      description = "User 프로필 이미지",
      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
  ) CreateReadStatusRequest request);

  @Operation(summary = "ReadStatus 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "ReadStatus가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "ReadStatus를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("User with id {readStatusId} not found"))
      ),
  })
  public ResponseEntity<ReadStatus> update(
      @Parameter(description = "수정 할 ReadStatus ID") UUID readStatusId
      , @Parameter(description = "수정 할 ReadStatus 정보") UpdateReadStatusRequest request);


  @Operation(summary = "해당 User의 ReadStatus 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "ReadStatus가 성공적으로 수정됨",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class)))
      ),
      @ApiResponse(
          responseCode = "404", description = "해당 User의 ReadStatus를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("User with id {userId} not found"))
      ),
  })
  public ResponseEntity<List<ReadStatus>> findByUserId(@Parameter(description = "조회 할 User ID") UUID userId);
}
