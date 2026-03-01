package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @Override
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> binaryContentCreateRequests = Optional.ofNullable(attachments)
        .map(list -> list.stream()
            .map(this::resolveBinaryRequest)
            .flatMap(Optional::stream)
            .toList())
        .orElse(List.of());
    Message createdMessage = messageService.create(messageCreateRequest,
        binaryContentCreateRequests);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @PatchMapping(
      path = "/{messageId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}

  )
  @Override
  public ResponseEntity<Message> update(
      @PathVariable UUID messageId,
      @RequestPart("messageUpdateRequest") MessageUpdateRequest messageUpdateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> binaryContentCreateRequests = Optional.ofNullable(attachments)
        .map(list -> list.stream()
            .map(this::resolveBinaryRequest)
            .flatMap(Optional::stream)
            .toList())
        .orElse(List.of());
    Message updateMessage = messageService.update(messageId, messageUpdateRequest,
        binaryContentCreateRequests);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updateMessage);
  }

  @DeleteMapping(
      path = "/{messageId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }


  @GetMapping(
      path = "/channel/{channelId}")
  @Override
  public ResponseEntity<List<Message>> findAllByChannelId(
      @PathVariable("channelId") UUID channelId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messageService.findAllByChannelId(channelId));
  }

  private Optional<BinaryContentCreateRequest> resolveBinaryRequest(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(new BinaryContentCreateRequest(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getBytes()
      ));
    } catch (IOException e) {
      throw new RuntimeException("파일 처리 중 오류", e);
    }
  }
}
