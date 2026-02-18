package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublicChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        Channel channel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channelService.find(channel.getId()));
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        Channel channel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(channelService.find(channel.getId()));
    }

    @GetMapping("/id/{channelId}")
    public ResponseEntity<ChannelDto> getChannel(
            @PathVariable UUID channelId
    ) {
        return ResponseEntity.ok(channelService.find(channelId));
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> getChannelsByUser(
            @RequestParam UUID userId
    ) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }

    @PutMapping("/{channelId}")
    public ResponseEntity<ChannelDto> updatePublicChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        channelService.update(channelId, request);
        return ResponseEntity.ok(channelService.find(channelId));
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
}

