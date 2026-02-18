package com.sprint.mission.discodeit.controller;

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
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/create")
    public ResponseEntity<Message> createMessage(
            @RequestBody MessageCreateRequest request
    ) {
        List<BinaryContentCreateRequest> attachments =
                request.attachments() != null ? request.attachments() : Collections.emptyList();

        Message created = messageService.create(request, attachments);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }
//    특정 메시지 조회
    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessage(
            @PathVariable UUID messageId
    ) {
        return ResponseEntity.ok(messageService.find(messageId));
    }
//    채널의 모든 메시지 조회
    @GetMapping
    public ResponseEntity<List<Message>> getMessagesByChannel(
            @RequestParam UUID channelId
    ) {
        return ResponseEntity.ok(
                messageService.findAllByChannelId(channelId)
        );
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        return ResponseEntity.ok(
                messageService.update(messageId, request)
        );
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }
}
