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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ResponseBody
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Controller
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(
            path = "create",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ){
        List<BinaryContentCreateRequest> binaryAttachments = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                try {
                    binaryAttachments.add(
                            new BinaryContentCreateRequest(
                                    file.getOriginalFilename(),
                                    file.getContentType(),
                                    file.getBytes()
                            )
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(file.getOriginalFilename());
                System.out.println(file.getSize());
                System.out.println(file.getContentType());

            }
        }

        Message createdMessage =
                messageService.create(messageCreateRequest, binaryAttachments);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMessage);


    }


    @RequestMapping(
            path = "update",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Message> update(
            @RequestParam("messageId") UUID messageId,
            @RequestPart("message") MessageUpdateRequest messageUpdateRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ){
        List<BinaryContentCreateRequest> binaryAttachments = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                try {
                    binaryAttachments.add(
                            new BinaryContentCreateRequest(
                                    file.getOriginalFilename(),
                                    file.getContentType(),
                                    file.getBytes()
                            )
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Message updatedMessage = messageService.update(messageId, messageUpdateRequest, binaryAttachments);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedMessage);
    }


    @RequestMapping(path = "delete")
    public ResponseEntity<Message> delete(
            @RequestParam("messageId") UUID messageId
    ){
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(
            path = "findAllByChannelId"
    )
    public ResponseEntity<List<Message>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId
    ){
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messages);
    }


}
