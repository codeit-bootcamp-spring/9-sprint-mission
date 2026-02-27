package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageView;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
@RequestMapping("/api/messages")
public interface MessageApi {

  @Operation(summary = "메시지 생성 (첨부파일 포함 가능)")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "생성 성공",
          content = @Content(schema = @Schema(implementation = MessageView.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  MessageView create(
      @Parameter(description = "메시지 생성 요청(JSON)")
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @Parameter(description = "첨부파일 목록")
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  );

  @Operation(summary = "메시지 수정")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "수정 성공",
          content = @Content(schema = @Schema(implementation = MessageView.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @PatchMapping(value = "/{messageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  MessageView update(
      @Parameter(description = "Message ID")
      @PathVariable UUID messageId,
      @Parameter(description = "수정 params")
      @RequestBody MessageUpdateRequest.Params params
  );

  @Operation(summary = "메시지 단건 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = MessageView.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @GetMapping("/{messageId}")
  MessageView findById(
      @Parameter(description = "Message ID")
      @PathVariable UUID messageId
  );

  @Operation(summary = "채널 기준 메시지 목록 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "조회 성공",
          content = @Content(schema = @Schema(implementation = MessageView.class))
      )
  })
  @GetMapping
  List<MessageView> findAllByChannelId(
      @Parameter(description = "Channel ID")
      @RequestParam("channelId") UUID channelId
  );

  @Operation(summary = "메시지 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))
      )
  })
  @DeleteMapping("/{messageId}")
  ResponseEntity<Void> delete(
      @Parameter(description = "Message ID")
      @PathVariable UUID messageId
  );
}
