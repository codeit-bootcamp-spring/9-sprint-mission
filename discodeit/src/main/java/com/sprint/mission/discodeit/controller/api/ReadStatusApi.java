package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "읽음 상태 관리 API")
public interface ReadStatusApi {

  @Operation(summary = "읽음 상태 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "읽음 상태 생성 성공",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 데이터",
          content = @Content(schema = @Schema(implementation = String.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "유저 또는 채널을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })
  ResponseEntity<ReadStatus> create(
      @Parameter(description = "읽음 상태 생성 정보") @RequestBody ReadStatusCreateRequest request
  );

  @Operation(summary = "읽음 상태 수정 (메시지 읽음 처리)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "읽음 상태 수정 성공",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "읽음 상태 데이터를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })
  ResponseEntity<ReadStatus> update(
      @Parameter(description = "수정할 읽음 상태 ID") @RequestParam("readStatusId") UUID readStatusId,
      @Parameter(description = "수정할 읽음 상태 정보") @RequestBody ReadStatusUpdateRequest request
  );

  @Operation(summary = "유저별 읽음 상태 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "읽음 상태 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatus.class)))
      ),
      @ApiResponse(
          responseCode = "404", description = "유저를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })
  ResponseEntity<List<ReadStatus>> findAllByUserId(
      @Parameter(description = "조회할 유저 ID") @RequestParam("userId") UUID userId
  );
}
