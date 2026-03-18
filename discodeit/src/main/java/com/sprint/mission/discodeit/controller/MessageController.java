package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;
  private final PageResponseMapper pageResponseMapper; // 🌟 매퍼 주입!

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Override
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    // 🌟 MultipartFile 리스트를 우리가 만든 DTO 리스트로 변환하는 로직 추가!
    List<BinaryContentCreateRequest> attachmentRequests = resolveAttachments(attachments);

    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessage);
  }

  @PatchMapping("/{messageId}")
  @Override
  public ResponseEntity<MessageDto> update(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    MessageDto updatedMessage = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @DeleteMapping("/{messageId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping("/channel/{channelId}") // 경로를 명확하게 지정해 줍니다.
  @Override
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @PathVariable("channelId") UUID channelId, // 🌟 경로 변수로 쏙!
      @PageableDefault(size = 50, sort = "createdAt", direction = Direction.DESC) Pageable pageable
      // 🌟 페이징 처리
  ) {
    Slice<MessageDto> messageSlice = messageService.findAllByChannelId(channelId, pageable);
    PageResponse<MessageDto> pageResponse = pageResponseMapper.fromSlice(messageSlice);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(pageResponse);
  }

  // 🌟 (도우미 메서드) 유저 컨트롤러에서 하셨던 것처럼, 파일을 변환해 주는 로직입니다.
  private List<BinaryContentCreateRequest> resolveAttachments(List<MultipartFile> attachments) {
    if (attachments == null || attachments.isEmpty()) {
      return Collections.emptyList();
    }
    return attachments.stream()
        .filter(file -> !file.isEmpty())
        .map(file -> {
          try {
            return new BinaryContentCreateRequest(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
            );
          } catch (IOException e) {
            throw new RuntimeException("Failed to read attachment", e);
          }
        })
        .toList();
  }
}