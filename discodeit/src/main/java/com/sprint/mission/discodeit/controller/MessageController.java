package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {
    private final MessageService messageService;
    private final BinaryContentService binaryContentService;

    @Timed("message.create.async")
    @PostMapping
    public ResponseEntity<MessageDto> send(@Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
                                        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

        log.info("메시지 전송 요청 수신: userId={}, channelId={}", request.authorId(), request.channelId());
        List<BinaryContentCreateRequest> binaryContentCreateRequests = Collections.emptyList();

        if (attachments != null) {
            binaryContentCreateRequests = attachments.stream()
                .map(attachment->{
                  try {
                    return new BinaryContentCreateRequest(
                      attachment.getOriginalFilename(),
                      attachment.getContentType(),
                      attachment.getSize(),
                        attachment.getBytes()
                    );
                  } catch (IOException e) {
                      log.error("메시지 첨부파일 데이터 읽기 실패: filename={}", attachment.getOriginalFilename(), e);
                      throw new RuntimeException(e);
                  }
                })
                .toList();
        }

        MessageDto newMsg = messageService.create(request, binaryContentCreateRequests);

        log.debug("메시지 전송 요청 처리 완료: userId={}, channelId={}", request.authorId(), request.channelId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newMsg);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(@PathVariable UUID messageId
        , @Valid @RequestBody MessageUpdateRequest request){
        log.info("메시지 수정 요청 수신: messageId={}", messageId);
        MessageDto msg = messageService.updateContent(messageId, request.content());
        log.debug("메시지 수정 요청 처리 완료: messageId={}", messageId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(msg);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId){
        log.info("메시지 삭제 요청 수신: messageId={}", messageId);
        messageService.delete(messageId);
        log.debug("메시지 삭제 요청 처리 완료: messageId={}", messageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageDto>> findByChannel(@RequestParam(value = "channelId") UUID channelId,
        @RequestParam(required = false) Instant cursor,
        @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        log.info("채널 메시지 목록 조회 요청 수신: channelId={}, cursor={}, pageSize={}",
            channelId, cursor, pageable.getPageSize());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messageService.findAllByChannelId(channelId, cursor, pageable));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDto> find(@PathVariable UUID messageId){
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messageService.findByID(messageId));
    }
}
