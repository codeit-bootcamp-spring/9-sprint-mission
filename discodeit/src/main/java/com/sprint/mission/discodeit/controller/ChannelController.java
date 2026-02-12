package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    // [1] 채널 생성 (공개/비공개 통합)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> create(@RequestBody ChannelCreateRequest request) {
        return channelService.createChannel(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    // [2] 특정 사용자가 접근 가능한 모든 채널 목록 조회
    @RequestMapping(value = "/findByUser", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse>> findByUserId(@RequestParam UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    // [3] 채널 정보 수정 (이름, 설명 등)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> update(@RequestParam UUID id,
                                                  @RequestParam String name,
                                                  @RequestParam String description) {
        return channelService.update(id, name, description)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // [4] 채널 삭제
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID id) {
        if (channelService.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}