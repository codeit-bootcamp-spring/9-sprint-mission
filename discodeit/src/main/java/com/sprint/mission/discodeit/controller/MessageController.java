package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @Override
  @PostMapping
  public ResponseEntity<MessageDto> create( // 💡 Message -> MessageDto
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
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

    // 💡 서비스에서 DTO를 반환하므로 타입을 변경합니다.
    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @Override
  @PatchMapping("update")
  public ResponseEntity<MessageDto> update(@RequestParam("messageId") UUID messageId, // 💡 Message -> MessageDto
      @RequestBody MessageUpdateRequest request) {
    MessageDto updatedMessage = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @Override
  @DeleteMapping("delete")
  public ResponseEntity<Void> delete(@RequestParam("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }


  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId( // 💡 List<Message> -> List<MessageDto>
      @RequestParam("channelId") UUID channelId,
      @RequestParam(name = "page", defaultValue = "0") int page) {
    PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId,page);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}