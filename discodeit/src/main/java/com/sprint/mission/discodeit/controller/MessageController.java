package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;


    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest")MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false)List<MultipartFile> attachments
            ) {
        List<BinaryContentCreateRequest> binaryContentCreateRequests = Optional.ofNullable(attachments)
                .map(list -> list.stream()
                        .map(this::resolveBinaryRequest)
                        .flatMap(Optional::stream)
                        .toList())
                .orElse(List.of());
        Message createdMessage = messageService.create(messageCreateRequest, binaryContentCreateRequests);
        return  ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);

    }

    @RequestMapping(
            path = "/update/{messageId}",
            method = RequestMethod.PUT
    )
    public ResponseEntity<Message> update(
            @PathVariable UUID messageId,
            @RequestPart("messageUpdateRequest") MessageUpdateRequest messageUpdateRequest
            ) {
        Message updateMessage = messageService.update(messageId, messageUpdateRequest);
        return  ResponseEntity.ok(updateMessage);
    }

    @RequestMapping(
            path = "/delete/{messageId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }


    @RequestMapping(
            path = "/channel/{channelId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<Message>> findAllByChannelId(@PathVariable UUID channelId)
    {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    private Optional<BinaryContentCreateRequest> resolveBinaryRequest(MultipartFile file) {
        if (file == null || file.isEmpty()) return Optional.empty();
        try{
            return Optional.of(new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            ));
        }catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류", e);
        }
    }
}
