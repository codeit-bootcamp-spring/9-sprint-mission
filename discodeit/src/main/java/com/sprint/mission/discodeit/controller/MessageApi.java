package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "메시지 관리 API")
public interface MessageApi {

  /**
   * v1.2 명세서 준수: GET /api/messages?channelId={uuid} 기존의 /api/channels/{channelId}/messages 경로에서
   * 변경되었습니다.
   */
  @Operation(summary = "Channel의 Message 목록 조회")
  @GetMapping("/api/messages")
  ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam(name = "channelId") UUID channelId, // PathVariable -> RequestParam
      @RequestParam(name = "lastMessageId", required = false) UUID lastMessageId,
      @RequestParam(name = "size", defaultValue = "50") int size);

  @Operation(summary = "Message 생성")
  @PostMapping(value = "/api/messages", consumes = "multipart/form-data")
  ResponseEntity<MessageDto> create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments);

  @Operation(summary = "Message 내용 수정")
  @PatchMapping("/api/messages/{messageId}")
  ResponseEntity<MessageDto> update(
      @PathVariable(name = "messageId") UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request);

  @Operation(summary = "Message 삭제")
  @DeleteMapping("/api/messages/{messageId}")
  ResponseEntity<Void> delete(@PathVariable(name = "messageId") UUID messageId);
}