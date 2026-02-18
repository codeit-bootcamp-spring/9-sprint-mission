package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 보내기 (특정 채널에)
    @PostMapping("/api/channels/{channelId}/messages")
    public Message create(
            @PathVariable("channelId") UUID channelId,
            @RequestBody CreateMessageBody body
    ) {
        // 1) body에서 꺼내기
        MessageCreateRequest raw = body.message();
        List<BinaryContentCreateRequest> files =
                (body.files() == null) ? Collections.emptyList() : body.files();

        // 2) ✅ URL의 channelId로 강제 덮어쓰기 (최소 변경 1안의 핵심)
        //    record 필드 순서: (content, channelId, authorId)
        MessageCreateRequest fixed = new MessageCreateRequest(
                raw.content(),
                channelId,
                raw.authorId()
        );

        // 3) 서비스 시그니처는 그대로 사용
        return messageService.create(fixed, files);
    }

    // 메시지 삭제
    @DeleteMapping("/api/messages/{messageId}")
    public void deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
    }

    // 특정 채널 메시지 목록 조회
    @GetMapping("/api/channels/{channelId}/messages")
    public List<Message> findAllByChannelId(@PathVariable("channelId") UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    // 메시지 단건 조회
    @GetMapping("/api/messages/{messageId}")
    public Message find(@PathVariable("messageId") UUID messageId) {
        return messageService.find(messageId);
    }

    // 메시지 수정
    @PatchMapping("/api/messages/{messageId}")
    public Message updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        return messageService.update(messageId, request);
    }

    // wrapper (유지)
    public record CreateMessageBody(
            MessageCreateRequest message,
            List<BinaryContentCreateRequest> files
    ) {}
}
