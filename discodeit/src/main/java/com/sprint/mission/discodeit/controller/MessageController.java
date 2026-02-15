package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;
    private final ChannelService channelService;
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Message> send(@RequestPart("message") CreateMessageRequest request,
                                        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        List<UUID> attachmentIds = new ArrayList<>();

        if (!attachments.isEmpty()) {
            for (MultipartFile file : attachments) {

                BinaryContent binaryContent = binaryContentService.uploadFile(file);

                attachmentIds.add(binaryContent.getId());
            }
        }

        Message newMsg = messageService.create(request, attachmentIds);

        return ResponseEntity.ok(newMsg);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Message> update(@RequestBody UpdateMessageRequest request){
        Message msg = messageService.updateContent(request.id(), request.content());
        return ResponseEntity.ok(msg);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void update(@RequestParam UUID id){
        UUID channelId = messageService.findByID(id).getChannelId();
        if (channelService.removeMessage(channelId, id)) {
            messageService.remove(id);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findByChannel(@RequestParam UUID channelId){
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }
}
