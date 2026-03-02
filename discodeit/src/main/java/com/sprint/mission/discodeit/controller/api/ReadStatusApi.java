package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.DTO.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.DTO.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "메시지 읽음 상태 관리 API")
public interface ReadStatusApi {

  @Operation(summary = "읽음 상태 생성", description = "새로운 읽음 상태를 등록합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "성공적으로 생성됨")
  })
  ResponseEntity<ReadStatus> create(ReadStatusCreateRequest request);

  @Operation(summary = "읽음 상태 수정", description = "특정 ID의 읽음 상태를 업데이트합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "404", description = "해당 ID를 찾을 수 없음")
  })
  ResponseEntity<ReadStatus> update(
      @Parameter(description = "수정할 읽음 상태 ID") UUID readStatusId,
      ReadStatusUpdateRequest request
  );

  @Operation(summary = "특정 유저의 읽음 상태 목록 조회", description = "유저 ID를 기준으로 모든 읽음 상태를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<ReadStatus>> findAllByUserId(
      @Parameter(description = "조회할 유저 ID") UUID userId
  );
}