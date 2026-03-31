package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @Override
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      UUID channelId, Instant cursor, int size) {
    log.debug("Fetching messages for channel: {}, cursor: {}, size: {}", channelId, cursor, size);
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId, cursor, size));
  }

  @Override
  public ResponseEntity<MessageDto> create(@Valid MessageCreateRequest request,
      List<MultipartFile> attachments) {
    log.info("Sending message to channel: {} by author: {}", request.channelId(),
        request.authorId());
    List<BinaryContentCreateRequest> attachmentRequests = resolveAttachmentRequests(attachments);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageService.send(request, attachmentRequests));
  }

  @Override
  public ResponseEntity<MessageDto> update(@PathVariable UUID messageId,
      @Valid MessageUpdateRequest request) {
    log.info("Updating message content: {}", messageId);
    return ResponseEntity.ok(messageService.update(messageId, request));
  }

  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    log.info("Deleting message: {}", messageId);
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  private List<BinaryContentCreateRequest> resolveAttachmentRequests(
      List<MultipartFile> attachments) {
    if (attachments == null || attachments.isEmpty()) {
      return Collections.emptyList();
    }
    return attachments.stream()
        .filter(f -> !f.isEmpty())
        .map(f -> {
          try {
            log.debug("Processing attachment: {}", f.getOriginalFilename());
            return new BinaryContentCreateRequest(f.getBytes(), f.getOriginalFilename(),
                f.getContentType(), f.getSize());
          } catch (IOException e) {
            log.error("Failed to extract binary data from file: {}", f.getOriginalFilename());
            throw new RuntimeException("파일 바이너리 데이터 추출 중 오류 발생", e);
          }
        }).toList();
  }
}