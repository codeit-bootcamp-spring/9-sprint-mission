package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi{

  @Operation(summary = "Message 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Message가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Message.class))
      )
  })
  public ResponseEntity<MessageDto> send(
      @Parameter(description = "Message 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) MessageCreateRequest request,
      @Parameter(description = "첨부할 파일 목록",
      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) List<MultipartFile> attachments);


  @Operation(summary = "Message 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = User.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("User with id {userId} not found"))
      )
  })
  public ResponseEntity<MessageDto> update(
      @Parameter(description = "수정 할 Message ID") UUID messageId
      , @Parameter(description = "수정할 Message 정보") MessageUpdateRequest request);

  @Operation(summary = "Message 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Message가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = " Message with id {messageId} not found"))
      )
  })
  public ResponseEntity<Void> delete(@Parameter(description = "삭제 할 Message ID") UUID messageId);

  @Operation(summary = "특정 Channel 내 Message 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message 조회 성공",
          content = @Content(schema = @Schema(implementation = Message.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = " Message with id {messageId} not found"))
      )
  })
  public ResponseEntity<PageResponse<MessageDto>> findByChannel(
      @Parameter(description = "조회 할 Channel ID") UUID channelId,
      @Parameter(description = "조회 시작 커서") Instant cursor,
      @Parameter(description = "페이지네이션 정보") Pageable pageable
  );


  @Operation(summary = "Message 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message 조회 성공",
          content = @Content(schema = @Schema(implementation = Message.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = " Message with id {messageId} not found"))
      )
  })
  public ResponseEntity<MessageDto> find(@Parameter(description = "조회 할 Message ID") UUID messageId);
}
