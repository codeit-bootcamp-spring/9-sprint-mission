package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @Override
  @Timed("message.create.async")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> create(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    log.debug("POST /api/messages - create message: channelId={}, authorId={}",
        messageCreateRequest.channelId(), messageCreateRequest.authorId());
    List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
                );
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());
    MessageResponse createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @Override
  @PatchMapping(path = "/{messageId}")
  public ResponseEntity<MessageResponse> update(@PathVariable UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request) {
    log.debug("PATCH /api/messages/{} - update message", messageId);
    MessageResponse updatedMessage = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @Override
  @DeleteMapping(path = "/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    log.debug("DELETE /api/messages/{} - delete message", messageId);
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Override
  @GetMapping
  public ResponseEntity<PageResponse<MessageResponse>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @RequestParam(value = "cursor", required = false) Instant cursor,
      Pageable pageable
  ) {
    PageResponse<MessageResponse> messages = messageService.findAllByChannelId(channelId, cursor, pageable);
    return ResponseEntity.ok(messages);
  }
}
