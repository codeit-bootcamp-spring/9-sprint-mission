package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "메시지 생성")
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "메시지 생성 성공",
          content = @Content(schema = @Schema(implementation = Message.class))
      )
  })
  ResponseEntity<Message> create(
      @Parameter(description = "메시지 생성 요청")
      MessageCreateRequest request
  );

  @Operation(summary = "채널 메시지 목록 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "메시지 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))
      )
  })
  ResponseEntity<List<Message>> findAllByChannelId(
      @Parameter(description = "채널 ID")
      UUID channelId
  );

  @Operation(summary = "메시지 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "메시지 수정 성공",
          content = @Content(schema = @Schema(implementation = Message.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "메시지를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Message not found"))
      )
  })
  ResponseEntity<Message> update(
      @Parameter(description = "메시지 ID")
      UUID messageId,

      @Parameter(description = "메시지 수정 요청")
      MessageUpdateRequest request
  );

  @Operation(summary = "메시지 삭제")
  @ApiResponses({
      @ApiResponse(
          responseCode = "204",
          description = "메시지 삭제 성공"
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "메시지 ID")
      UUID messageId
  );
}