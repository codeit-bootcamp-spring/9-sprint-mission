package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateFullRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public Message create(@RequestBody MessageCreateFullRequest request) {
        return messageService.create(
                request.message(),
                request.attachments() == null ? List.of() : request.attachments()
        );
    }

    @PatchMapping("/{messageId}")
    public Message update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    @DeleteMapping("/{messageId}")
    public void delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }

    @GetMapping("/channel/{channelId}")
    public List<Message> findAllByChannel(@PathVariable UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }
}
