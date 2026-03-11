package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

  @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message 읽음 상태가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = ReadStatusDto.class))),
      @ApiResponse(responseCode = "400", description = "이미 읽음 상태가 존재함",
          content = @Content(examples = @ExampleObject("ReadStatus with userId {userId} and channelId {channelId} already exists"))),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Channel | User with id {channelId | userId} not found")))
  })
  public ResponseEntity<ReadStatusDto> create(
      @Parameter(description = "생성된 읽음 상태") @RequestBody ReadStatusCreateRequest request);


  @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message 읽음 상태가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ReadStatusDto.class))),
      @ApiResponse(responseCode = "404", description = "Message 읽음 상태를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("ReadStatus with id {readStatusId} not found")))
  })
  public ResponseEntity<ReadStatusDto> update(
      @Parameter(description = "수정할 읽음 상태 ID", required = true) @PathVariable UUID readStatusId,
      @Parameter(content = @Content(schema = @Schema(implementation = ReadStatusUpdateRequest.class))) @RequestBody ReadStatusUpdateRequest request);

  @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
  @ApiResponse(responseCode = "200", description = "Message 읽음 상태 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusDto.class))))
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(
      @Parameter(description = "조회할 User ID", required = true) @RequestParam UUID userId);

}
