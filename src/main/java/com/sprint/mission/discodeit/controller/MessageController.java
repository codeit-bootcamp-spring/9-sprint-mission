package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
    private final PageResponseMapper pageResponseMapper;

    public MessageController(
        MessageService messageService,
        ObjectMapper objectMapper,
        PageResponseMapper pageResponseMapper
    ) {
        this.messageService = messageService;
        this.objectMapper = objectMapper;
        this.pageResponseMapper = pageResponseMapper;
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
    public PageResponse<MessageDto> getMessages(
        @RequestParam UUID channelId,
        @RequestParam(defaultValue = "0") int page
    ) {

        Pageable pageable = PageRequest.of(
            page,
            50,
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Slice<MessageDto> slice = messageService.getMessages(channelId, pageable);

        return pageResponseMapper.fromSlice(slice);
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}