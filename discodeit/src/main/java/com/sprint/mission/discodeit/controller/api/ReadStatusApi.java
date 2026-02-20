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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "메시지 수신 상태 API")
@RequestMapping("/api/readStatuses")
public interface ReadStatusApi {

  @Operation(
      summary = "수신 상태 생성",
      description = "메시지의 수신 상태를 생성합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "생성 성공",
          content = @Content(schema = @Schema(implementation = ReadStatus.class)))
  })
  @PostMapping
  ResponseEntity<ReadStatus> createReadStatus(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "수신 상태 생성 요청",
          required = true
      )
      @RequestBody ReadStatusCreateRequest request
  );


  @Operation(
      summary = "수신 상태 수정",
      description = "readStatusId로 수신 상태를 수정합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = ReadStatus.class))),
      @ApiResponse(responseCode = "404", description = "수신 상태를 찾을 수 없음")
  })
  @PatchMapping("/{readStatusId}")
  ResponseEntity<ReadStatus> updateReadStatus(

      @Parameter(
          description = "수신 상태 ID",
          required = true
      )
      @PathVariable UUID readStatusId,

      @RequestBody ReadStatusUpdateRequest request
  );


  @Operation(
      summary = "수신 상태 목록 조회",
      description = "userId로 수신 상태 목록을 조회합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회 성공",
          content = @Content(array = @ArraySchema(
              schema = @Schema(implementation = ReadStatus.class)
          )))
  })
  @GetMapping
  ResponseEntity<List<ReadStatus>> getReadStatuses(

      @Parameter(
          description = "사용자 ID",
          required = true
      )
      @RequestParam UUID userId
  );
}