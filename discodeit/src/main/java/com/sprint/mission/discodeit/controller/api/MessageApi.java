package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "메시지 전송 (파일 첨부 가능)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "메시지 전송 성공",
          content = @Content(schema = @Schema(implementation = Message.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 데이터",
          content = @Content(schema = @Schema(implementation = String.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "채널 또는 작성자를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })
  ResponseEntity<Message> create(
      @Parameter(
          description = "메시지 생성 정보 (JSON)",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,

      @Parameter(
          description = "첨부 파일 목록 (선택)",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      )
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  );

  @Operation(summary = "메시지 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "메시지 수정 성공",
          content = @Content(schema = @Schema(implementation = Message.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "메시지를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })
  ResponseEntity<Message> update(
      @Parameter(description = "수정할 메시지 ID") UUID messageId,
      @Parameter(description = "수정할 메시지 내용") @RequestBody MessageUpdateRequest request
  );

  @Operation(summary = "메시지 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "메시지 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "404", description = "메시지를 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 메시지 ID") UUID messageId
  );

  @Operation(summary = "채널별 메시지 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "메시지 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = Message.class)))
      ),
      @ApiResponse(
          responseCode = "404", description = "채널을 찾을 수 없음",
          content = @Content(schema = @Schema(implementation = String.class))
      )
  })
  ResponseEntity<List<Message>> findAllByChannelId(
      @Parameter(description = "조회할 채널 ID") UUID channelId
  );
}
