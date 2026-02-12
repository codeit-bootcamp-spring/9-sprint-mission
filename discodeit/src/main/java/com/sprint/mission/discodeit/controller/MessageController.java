package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDeleteRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageView;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public MessageView create(
            @RequestBody MessageCreateRequest request) {
        return messageService.create(request);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PUT)
    public MessageView update(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest.Params params
    ) {
        return messageService.update(new MessageUpdateRequest(messageId, params));
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
    public MessageView findById(@PathVariable("messageId") UUID messageId) {
        return messageService.findById(messageId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<MessageView> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(new MessageDeleteRequest(messageId));
    }
}
