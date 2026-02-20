package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping
  @Override
  public ResponseEntity<Message> create(
      @RequestBody MessageCreateRequest request
  ) {
    List<BinaryContentCreateRequest> attachments =
        request.attachments() != null
            ? request.attachments()
            : Collections.emptyList();

    Message created = messageService.create(request, attachments);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(created);
  }

  @GetMapping
  @Override
  public ResponseEntity<List<Message>> findAllByChannelId(
      @RequestParam UUID channelId
  ) {
    return ResponseEntity.ok(
        messageService.findAllByChannelId(channelId)
    );
  }

  @PatchMapping("/{messageId}")
  @Override
  public ResponseEntity<Message> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    return ResponseEntity.ok(
        messageService.update(messageId, request)
    );
  }

  @DeleteMapping("/{messageId}")
  @Override
  public ResponseEntity<Void> delete(
      @PathVariable UUID messageId
  ) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }
}