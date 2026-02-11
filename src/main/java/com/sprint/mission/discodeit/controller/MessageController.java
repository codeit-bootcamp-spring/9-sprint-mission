package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;


    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public MessageResponse createMessage(@RequestBody CreateMessageRequest request) {
        return messageService.create(request);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
    public MessageResponse updateMessage(
            @PathVariable UUID messageId,
            @RequestBody UpdateMessageRequest request
    ) {
        return messageService.update(messageId, request);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public List<MessageResponse> getMessages(@PathVariable UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponse> getMessagesByChannel(
            @RequestParam UUID channelId
    ) {
        return messageService.findAllByChannelId(channelId);
    }

}
