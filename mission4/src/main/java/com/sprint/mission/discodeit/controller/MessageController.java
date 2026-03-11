package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.awt.Cursor;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;

  @Override
  @PostMapping(
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> Requests
  ) {
    List<BinaryContentCreateRequest> attachmentIds = binaryContentCreateRequest(Requests);
    MessageDto message = messageService.create(messageCreateRequest, attachmentIds);
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @Override
  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findByAllChannelId(
      @RequestParam UUID channelId,
      @RequestParam(required = false) LocalDateTime cursor,
      @PageableDefault(size = 50, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    PageResponse<MessageDto> messageList = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    return ResponseEntity.ok(messageList);
  }

  @Override
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest
  ) {
    MessageDto updateMessage = messageService.update(messageId, messageUpdateRequest);
    return ResponseEntity.ok(updateMessage);
  }

  @Override
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID messageId
  ) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }


  public List<BinaryContentCreateRequest> binaryContentCreateRequest(
      List<MultipartFile> attachmentIds) {
    if (attachmentIds == null || attachmentIds.isEmpty()) {
      return List.of();
    }
    return attachmentIds.stream()
        .map(file -> {
          try {
            return new BinaryContentCreateRequest(

                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
            );
          } catch (IOException e) {
            throw new RuntimeException("파일 생성 중 오류발생", e);
          }
        }).toList();


  }
}
