package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<Channel> createPublic(@RequestBody ChannelCreateRequest request) {
        Channel channel = channelService.createPublic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @PostMapping("/private")
    public ResponseEntity<Channel> createPrivate(@RequestBody ChannelCreateRequest request) {
        Channel channel = channelService.createPrivate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@PathVariable UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChannelDto> update(
            @PathVariable UUID id,
            @RequestBody ChannelUpdateRequest request) {
        Channel updatedChannel = channelService.update(id, request);
        return ResponseEntity.ok(toDto(updatedChannel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ChannelDto toDto(Channel channel) {
        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                new ArrayList<>(), // 참여자 명단은 일단 빈 리스트로 처리
                Instant.now()
        );
    }

    @GetMapping
    public ResponseEntity<List<Channel>> findAll() {
        return ResponseEntity.ok(null);
    }
}