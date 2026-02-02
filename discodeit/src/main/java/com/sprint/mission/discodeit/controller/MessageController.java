package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public Message create(
            @RequestParam String content,
            @RequestParam UUID channelId,
            @RequestParam UUID authorId
            ) {
        return messageService.create(content, channelId, authorId);
    }

    @GetMapping("/channel/{channelId}")
    public List<Message> findByChannel(@PathVariable UUID channelId) {
        return messageService.findByChannelId(channelId);
    }

    @DeleteMapping("/{messageId}")
    public void delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}
