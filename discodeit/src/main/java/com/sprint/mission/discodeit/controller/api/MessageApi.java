package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.DTO.request.MessageCreateRequest;
import com.sprint.mission.discodeit.DTO.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 관리 API")
public interface MessageApi {

  @Operation(summary = "메시지 작성 (파일 첨부 가능)")
  @ApiResponse(responseCode = "201", description = "메시지 생성됨")
  ResponseEntity<Message> create(
      @Parameter(description = "메시지 생성 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) MessageCreateRequest messageCreateRequest,
      @Parameter(description = "첨부 파일 리스트") List<MultipartFile> attachments
  );

  @Operation(summary = "메시지 수정")
  @ApiResponse(responseCode = "200", description = "수정 성공")
  ResponseEntity<Message> update(
      @Parameter(description = "수정할 메시지 ID") UUID messageId,
      MessageUpdateRequest request
  );

  @Operation(summary = "메시지 삭제")
  @ApiResponse(responseCode = "204", description = "삭제 성공")
  ResponseEntity<Void> delete(@Parameter(description = "삭제할 메시지 ID") UUID messageId);

  @Operation(summary = "채널별 메시지 조회")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  ResponseEntity<List<Message>> findAllByChannelId(
      @Parameter(description = "채널 ID") UUID channelId);
}