package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    //생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Message> sendMessage(  @PathVariable UUID channelId,
                                                 @RequestBody MessageCreateRequest request) {
        Message message = messageService.create(request, List.of());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    //수정
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Message> updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request) {

        Message message = messageService.update(messageId, request);
        return ResponseEntity.ok(message);
    }

    //삭제
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    //특정 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessages(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

}
