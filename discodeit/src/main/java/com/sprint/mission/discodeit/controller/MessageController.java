package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
@Slf4j
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    log.debug("메시지 생성 시도: channelId={}, content={}", messageCreateRequest.channelId(),
        messageCreateRequest.content());

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
                log.error("첨부파일 변환 실패: filename={}", file.getOriginalFilename(), e);
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());

    MessageDto createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
    log.info("메시지 생성 완료: messageId={}, channelId={}, attachmentsCount={}",
        createdMessage.id(), createdMessage.channelId(), attachmentRequests.size());

    return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
  }

  @PatchMapping(path = "{messageId}")
  public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    log.debug("메시지 업데이트 시도: messageId={}, newContent={}", messageId, request.newContent());
    MessageDto updatedMessage = messageService.update(messageId, request);
    log.info("메시지 업데이트 완료: messageId={}", updatedMessage.id());
    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @DeleteMapping(path = "{messageId}")
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    log.debug("메시지 삭제 시도: messageId={}", messageId);
    messageService.delete(messageId);
    log.info("메시지 삭제 완료: messageId={}", messageId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @RequestParam(value = "cursor", required = false) Instant cursor,
      @PageableDefault(
          size = 50,
          page = 0,
          sort = "createdAt",
          direction = Direction.DESC
      ) Pageable pageable) {
    log.debug("채널 메시지 조회 시도: channelId={}, cursor={}", channelId, cursor);
    PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor, pageable);
    log.info("채널 메시지 조회 완료: channelId={}, 조회수={}", channelId, messages.content().size());
    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }
}