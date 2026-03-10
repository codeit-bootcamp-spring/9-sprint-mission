package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @Override
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      UUID channelId, UUID lastMessageId, int size) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId, lastMessageId, size));
  }

  @Override
  public ResponseEntity<MessageDto> create(MessageCreateRequest request,
      List<MultipartFile> attachments) {
    List<BinaryContentCreateRequest> attachmentRequests = resolveAttachmentRequests(attachments);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageService.send(request, attachmentRequests));
  }

  @Override
  public ResponseEntity<MessageDto> update(UUID messageId, MessageUpdateRequest request) {
    return ResponseEntity.ok(messageService.update(messageId, request));
  }

  @Override
  public ResponseEntity<Void> delete(UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  private List<BinaryContentCreateRequest> resolveAttachmentRequests(
      List<MultipartFile> attachments) {
    if (attachments == null) {
      return Collections.emptyList();
    }
    return attachments.stream()
        .filter(f -> !f.isEmpty())
        .map(f -> {
          try {
            return new BinaryContentCreateRequest(f.getBytes(), f.getOriginalFilename(),
                f.getContentType(), f.getSize());
          } catch (IOException e) {
            throw new RuntimeException("파일 바이너리 데이터 추출 중 오류 발생", e);
          }
        }).toList();
  }
}