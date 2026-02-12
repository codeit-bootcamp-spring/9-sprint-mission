package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    // [1] 메시지 전송
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> send(@RequestBody MessageCreateRequest request) {
        return ResponseEntity.ok(messageService.send(request));
    }

    // [2] 특정 채널의 모든 메시지 목록 조회
    @RequestMapping(value = "/findByChannel", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> findByChannel(@RequestParam UUID channelId) {
        return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
    }

    // [3] 특정 메시지 삭제 (첨부파일 포함)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID id) {
        if (messageService.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}