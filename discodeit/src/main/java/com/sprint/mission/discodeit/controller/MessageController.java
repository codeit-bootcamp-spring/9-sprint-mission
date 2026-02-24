package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @Override
  public ResponseEntity<List<MessageResponse>> findAllByChannelId(@RequestParam UUID channelId) {
    List<MessageResponse> responses = messageService.findAllByChannelId(channelId).stream()
        .map(this::convertToResponse)
        .toList();
    return ResponseEntity.ok(responses);
  }

  @Override
  public ResponseEntity<MessageResponse> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> attachmentRequests = resolveAttachmentRequests(attachments);
    Message message = messageService.send(request, attachmentRequests);
    return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(message));
  }

  @Override
  public ResponseEntity<MessageResponse> update(
      @PathVariable UUID messageId,
      @Valid @RequestBody MessageUpdateRequest request
  ) {
    Message message = messageService.update(messageId, request);
    return ResponseEntity.ok(convertToResponse(message));
  }

  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    return messageService.delete(messageId) ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }

  private MessageResponse convertToResponse(Message m) {
    return new MessageResponse(
        m.getId(),
        m.getCreatedAt(),
        m.getUpdatedAt(),
        m.getContent(),
        m.getChannelId(),
        m.getAuthorId(),
        m.getAttachmentIds()
    );
  }

  private List<BinaryContentCreateRequest> resolveAttachmentRequests(
      List<MultipartFile> attachments) {
    if (attachments == null) {
      return new ArrayList<>();
    }
    return attachments.stream()
        .filter(f -> !f.isEmpty())
        .map(f -> {
          try {
            return new BinaryContentCreateRequest(
                f.getBytes(),
                f.getContentType(),
                f.getOriginalFilename(),
                f.getSize()
            );
          } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류 발생", e);
          }
        })
        .toList();
  }
}