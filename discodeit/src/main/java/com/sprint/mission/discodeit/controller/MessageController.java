package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {
    private final MessageService messageService;
    private final BinaryContentService binaryContentService;

    @PostMapping
    public ResponseEntity<Message> send(@RequestPart("messageCreateRequest") MessageCreateRequest request,
                                        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        List<UUID> attachmentIds = new ArrayList<>();

        if (attachments != null && !attachments.isEmpty()) {

//            List<BinaryContent> binaryContents = binaryContentService.uploadFiles(attachments);
//            attachmentIds = binaryContents.stream()
//                .map(BinaryContent::getId)
//                .toList();
        }

        Message newMsg = messageService.create(request, attachmentIds);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newMsg);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> update(@PathVariable UUID messageId
        , @RequestBody MessageUpdateRequest request){
        Message msg = messageService.updateContent(messageId, request.content());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(msg);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId){
        messageService.remove(messageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<Message>> findByChannel(@RequestParam(value = "channelId") UUID channelId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messageService.findAllByChannelId(channelId));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<Message> find(@PathVariable UUID messageId){
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messageService.findByID(messageId));
    }
}
