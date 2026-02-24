package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateFullRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @PostMapping
    @Override
    public ResponseEntity<MessageDto> create(
        @RequestBody MessageCreateRequest request
    ) {
        MessageDto message = messageService.create(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(message);
    }

    @Override
    public ResponseEntity<MessageDto> find(UUID messageId) {
        return null;
    }

    @PatchMapping("/{messageId}")
    @Override
    public ResponseEntity<MessageDto> update(
        @PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest request
    ) {
        MessageDto updatedMessage = messageService.update(messageId, request);

        return ResponseEntity
            .ok(updatedMessage);
    }

    @DeleteMapping("/{messageId}")
    @Override
    public ResponseEntity<Void> delete(
        @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity
            .noContent()
            .build();
    }

    @GetMapping
    @Override
    public ResponseEntity<List<MessageDto>> findByChannelId(
        @RequestParam UUID channelId
    ) {
        List<MessageDto> messages = messageService.findByChannelId(channelId);

        return ResponseEntity
            .ok(messages);
    }
}
