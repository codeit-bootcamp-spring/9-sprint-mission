package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "Channel의 Message 목록 조회")
  @GetMapping("/api/messages")
  ResponseEntity<List<MessageResponse>> findAllByChannelId(@RequestParam UUID channelId);

  @Operation(summary = "Message 생성")
  @PostMapping(value = "/api/messages", consumes = "multipart/form-data")
  ResponseEntity<MessageResponse> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments);

  @Operation(summary = "Message 내용 수정")
  @PatchMapping("/api/messages/{messageId}")
  ResponseEntity<MessageResponse> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request);

  @Operation(summary = "Message 삭제")
  @DeleteMapping("/api/messages/{messageId}")
  ResponseEntity<Void> delete(@PathVariable UUID messageId);
}