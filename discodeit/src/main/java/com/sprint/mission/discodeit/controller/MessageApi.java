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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "Message API")
public interface MessageApi {

  @Operation(summary = "Channel의 Message 목록 조회")
  @GetMapping
  ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam(name = "channelId") UUID channelId,
      @RequestParam(name = "cursor", required = false) Instant cursor,
      @RequestParam(name = "size", defaultValue = "50") int size);

  @Operation(summary = "Message 생성")
  @PostMapping(consumes = "multipart/form-data")
  ResponseEntity<MessageDto> create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments);

  @Operation(summary = "Message 내용 수정")
  @PatchMapping("/{messageId}")
  ResponseEntity<MessageDto> update(
      @PathVariable(name = "messageId") UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request);

  @Operation(summary = "Message 삭제")
  @DeleteMapping("/{messageId}")
  ResponseEntity<Void> delete(@PathVariable(name = "messageId") UUID messageId);
}