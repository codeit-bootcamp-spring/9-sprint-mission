package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    public MessageController(MessageService messageService,
        ObjectMapper objectMapper) {
        this.messageService = messageService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageDto createMessage(
        @RequestParam("messageCreateRequest") MultipartFile messageCreateRequestFile,
        @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments
    ) throws IOException {

        String jsonStr = new String(
            messageCreateRequestFile.getBytes(),
            StandardCharsets.UTF_8
        );

        MessageCreateRequest request =
            objectMapper.readValue(jsonStr, MessageCreateRequest.class);

        return messageService.create(request, attachments);
    }

    @PatchMapping("/{messageId}")
    public MessageDto updateMessage(
        @PathVariable UUID messageId,
        @RequestBody MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    @GetMapping
    public List<MessageDto> getMessagesByChannel(
        @RequestParam UUID channelId
    ) {
        return messageService.findAllByChannelId(channelId);
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}