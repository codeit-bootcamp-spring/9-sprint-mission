package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    /**
     * ✅ 메시지 전송
     */
    @RequestMapping(method = RequestMethod.POST)
    public Message sendMessage(
            @RequestBody MessageCreateRequest request
    ) {
        return messageService.create(request, Optional.empty());
    }

    /**
     * ✅ 메시지 단건 조회
     */
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.GET
    )
    public Message find(
            @PathVariable UUID messageId
    ) {
        return messageService.find(messageId);
    }

    /**
     * ✅ 특정 채널의 메시지 목록 조회
     */
    @RequestMapping(
            value = "/channel/{channelId}",
            method = RequestMethod.GET
    )
    public List<Message> findByChannel(
            @PathVariable UUID channelId
    ) {
        return messageService.findAllByChannelId(channelId);
    }

    /**
     * ✅ 메시지 수정
     */
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.PATCH
    )
    public Message update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    /**
     * ✅ 메시지 삭제
     */
    @RequestMapping(
            value = "/{messageId}",
            method = RequestMethod.DELETE
    )
    public void delete(
            @PathVariable UUID messageId
    ) {
        messageService.delete(messageId);
    }
}
