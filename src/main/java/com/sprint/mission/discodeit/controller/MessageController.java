package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponse createMessage(
        @RequestParam(value = "messageCreateRequest", required = false) MultipartFile messageCreateRequestFile,
        @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments
    ) throws IOException {

        if (messageCreateRequestFile == null) {
            throw new IllegalArgumentException("messageCreateRequest 파일을 전달해주세요");
        }

        String jsonStr = new String(messageCreateRequestFile.getBytes(), StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        MessageCreateRequest request = objectMapper.readValue(jsonStr, MessageCreateRequest.class);

        return messageService.create(request, attachments);
    }

    @PatchMapping("/{messageId}")
    public MessageResponse updateMessage(
        @PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    @GetMapping
    public List<MessageResponse> getMessagesByChannel(
        @RequestParam UUID channelId
    ) {
        return messageService.findAllByChannelId(channelId);
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}
