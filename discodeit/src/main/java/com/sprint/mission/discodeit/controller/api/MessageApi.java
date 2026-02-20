package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
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
import java.util.List;
import java.util.UUID;
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
  public ResponseEntity<Message> send(
      @Parameter(description = "Message 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) CreateMessageRequest request,
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
  public ResponseEntity<Message> update(
      @Parameter(description = "수정 할 Message ID") UUID messageId
      , @Parameter(description = "수정할 Message 정보") UpdateMessageRequest request);

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


  @Operation(summary = "해당 Channel의 Message 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))
      )
  })
  public ResponseEntity<List<Message>> findByChannel(@Parameter(description = "조회 할 Channel ID") UUID channelId);


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
  public ResponseEntity<Message> find(@Parameter(description = "조회 할 Message ID") UUID messageId);
}
