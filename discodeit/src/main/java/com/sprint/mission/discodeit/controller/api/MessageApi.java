package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
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
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "Message 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Message가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Message.class))
      )
  })
  ResponseEntity<Message> create(
      @Parameter(
          description = "Message 내용",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) MessageCreateRequest messageCreateRequest,
      @Parameter(
          description = "첨부파일",
          content = @Content(schema = @Schema(implementation = Message.class))
      ) List<MultipartFile> attachments

  );

  @Operation(summary = "Message 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Message가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = Message.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Messages with id {messageId} not found"))
      )
  })
  ResponseEntity<Message> update(
      @Parameter(description = "수정할 Message ID") UUID messageId,
      @Parameter(description = "수정할 Message 정보") MessageUpdateRequest updateRequest,
      @Parameter(description = "수정할 Message 첨부파일") List<MultipartFile> attachments
  );

  @Operation(summary = "Message 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "Message가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {id} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Message ID") UUID messageId
  );

  @Operation(summary = "Message 검색")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Message 검색 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))
      )
  })
  ResponseEntity<List<Message>> findAllByChannelId(
      @Parameter(description = "검색할 ChannelID") UUID channelId
  );

}
