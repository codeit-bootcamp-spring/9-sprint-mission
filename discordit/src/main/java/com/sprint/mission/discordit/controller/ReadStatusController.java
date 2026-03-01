package com.sprint.mission.discordit.controller;

import com.sprint.mission.discordit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discordit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discordit.entity.ReadStatus;
import com.sprint.mission.discordit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "읽음 상태 API", description = "채널별 읽음 상태를 조회할 수 있다.")
@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "readStatus 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "readStatus 생성 성공"),
      @ApiResponse(responseCode = "404", description = "사용자 혹은 채널을 찾지 못함", content = @Content)
  })
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ReadStatus> create(
      @RequestBody ReadStatusCreateRequest request) {
    ReadStatus createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @Operation(summary = "readStatus 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "readStatus 수정 성공"),
      @ApiResponse(responseCode = "404", description = "readStatus ID를 찾지 못함", content = @Content)
  })
  @RequestMapping(path = "{readStatusId}", method = RequestMethod.PATCH)
  public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatus);
  }

  @Operation(summary = "readStatus 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "readStatus 조회 성공")
  })
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ReadStatus>> findAllByUserId(
      @RequestParam("userId") UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }
}
