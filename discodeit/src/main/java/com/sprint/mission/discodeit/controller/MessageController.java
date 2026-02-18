package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ResponseEntity<Message> sendMessage(@RequestBody MessageCreateRequest request) {
        return ResponseEntity.ok(messageService.create(request, List.of()));
    }
    @RequestMapping(value = "/message/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Message> updateMessage(@PathVariable UUID id, @RequestBody MessageUpdateRequest request){
        return ResponseEntity.ok(messageService.update(id, request));
    }
    @RequestMapping(value = "/message/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
