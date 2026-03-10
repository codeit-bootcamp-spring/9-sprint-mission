package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageDto createMessage(
        @RequestParam("messageCreateRequest") MultipartFile messageCreateRequestFile,
        @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments
    ) throws IOException {
        String jsonStr = new String(messageCreateRequestFile.getBytes(), StandardCharsets.UTF_8);
        MessageCreateRequest request = objectMapper.readValue(jsonStr, MessageCreateRequest.class);
        return messageService.create(request, attachments);
    }

    @PatchMapping("/{messageId}")
    public MessageDto updateMessage(@PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest request) {
        return messageService.update(messageId, request);
    }

    @GetMapping
    public PageResponse<MessageDto> getMessages(
        @RequestParam UUID channelId,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "50") int size
    ) {

        Pageable pageable = PageRequest.of(
            0,
            size,
            Sort.by(Sort.Direction.DESC, "createdAt")
                .and(Sort.by(Sort.Direction.DESC, "id"))
        );

        Instant cursorInstant = null;
        UUID lastId = null;

        if (cursor != null && !cursor.isBlank()) {
            if (cursor.contains("|")) {
                String[] parts = cursor.split("\\|");
                cursorInstant = Instant.parse(parts[0]);
                lastId = UUID.fromString(parts[1]);
            }
        }

        return messageService.getMessages(channelId, cursorInstant, lastId, pageable);
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}