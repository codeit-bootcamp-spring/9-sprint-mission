package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.PageResponse;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) throws IOException {
    List<BinaryContentCreateRequest> attachmentRequests = resolveAttachmentRequests(attachments);
    MessageDto createdMessage = messageService.create(request, attachmentRequests);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor
  ) {
    PageResponse<MessageDto> response =
        messageService.findAllByChannelId(channelId, cursor);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    MessageDto updatedMessage = messageService.update(messageId, request);
    return ResponseEntity.ok(updatedMessage);
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  private List<BinaryContentCreateRequest> resolveAttachmentRequests(
      List<MultipartFile> attachments) throws IOException {
    if (attachments == null || attachments.isEmpty()) {
      return new ArrayList<>();
    }
    List<BinaryContentCreateRequest> result = new ArrayList<>();
    for (MultipartFile file : attachments) {
      result.add(new BinaryContentCreateRequest(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getBytes()
      ));
    }
    return result;
  }
}