package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 보내기 (특정 채널에 메시지 생성)
    @PostMapping("/channels/{channelId}/messages")
    public Message createMessage(
            @PathVariable UUID channelId,
            @RequestBody MessageCreateRequest request
    ) {
        // Body에 channelId가 오더라도, URL channelId를 기준으로 강제 고정
        MessageCreateRequest fixed = new MessageCreateRequest(
                request.content(),
                channelId,
                request.authorId()
        );

        // 첨부파일 없는 버전: 빈 리스트 전달
        return messageService.create(fixed, List.of());
    }

    // 메시지 수정
    @PutMapping("/messages/{messageId}")
    public Message updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    // 메시지 삭제
    @DeleteMapping("/messages/{messageId}")
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }

    // 특정 채널의 메시지 목록 조회
    @GetMapping("/channels/{channelId}/messages")
    public List<Message> getMessagesByChannel(@PathVariable UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }
}

