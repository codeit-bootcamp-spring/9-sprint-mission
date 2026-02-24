package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/createMessage")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageCreateRequest messageCreateRequest)
    {
        List<BinaryContentCreateRequest> binaryContentCreateRequests = List.of();
        Message message = messageService.create(messageCreateRequest, binaryContentCreateRequests);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/updateMessage/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable UUID messageId,
                                                 @RequestBody MessageUpdateRequest messageUpdateRequest)
    {
        Message message = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/deleteMessage/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId)
    {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findMessage/channel/{channelId}")
    public ResponseEntity<List<Message>> findMessage(@PathVariable UUID channelId)
    {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

}
