package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "ReadStatus API")
@RequestMapping("/api/read-statuses")
public interface ReadStatusApi {

  @Operation(summary = "메시지 수신 정보 생성")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "생성 성공",
          content = @Content(schema = @Schema(implementation = ReadStatusView.class))
      ),
      @ApiResponse(responseCode = "400", description = "Bad Request")
  })
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ReadStatusView create(
      @Parameter(description = "생성 요청")
      @RequestBody ReadStatusCreateRequest request
  );

  @Operation(summary = "메시지 수신 정보 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "수정 성공",
          content = @Content(schema = @Schema(implementation = ReadStatusView.class))
      ),
      @ApiResponse(responseCode = "404", description = "Not Found")
  })
  @PatchMapping(value = "/{readStatusId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  ReadStatusView update(
      @Parameter(description = "ReadStatus ID")
      @PathVariable UUID readStatusId,
      @Parameter(description = "수정 params")
      @RequestBody ReadStatusUpdateRequest.Params params
  );

  @Operation(summary = "사용자 기준 메시지 수신 정보 목록 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = ReadStatusView.class))
      )
  })
  @GetMapping
  List<ReadStatusView> findAllByUserId(
      @Parameter(description = "User ID")
      @RequestParam("userId") UUID userId
  );
}
