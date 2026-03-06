package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
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
    public ResponseEntity<MessageDto> send(@RequestPart("messageCreateRequest") MessageCreateRequest request,
                                        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

        List<BinaryContentCreateRequest> binaryContentCreateRequests = Collections.emptyList();

        if (attachments != null) {
            binaryContentCreateRequests = attachments.stream()
                .map(attachment->{
                  try {
                    return new BinaryContentCreateRequest(
                      attachment.getOriginalFilename(),
                      attachment.getContentType(),
                      attachment.getSize(),
                        attachment.getBytes()
                    );
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                })
                .toList();
        }

        MessageDto newMsg = messageService.create(request, binaryContentCreateRequests);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newMsg);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageDto> update(@PathVariable UUID messageId
        , @RequestBody MessageUpdateRequest request){
        MessageDto msg = messageService.updateContent(messageId, request.content());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(msg);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId){
        messageService.delete(messageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> findByChannel(@RequestParam(value = "channelId") UUID channelId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messageService.findAllByChannelId(channelId));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDto> find(@PathVariable UUID messageId){
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messageService.findByID(messageId));
    }
}
