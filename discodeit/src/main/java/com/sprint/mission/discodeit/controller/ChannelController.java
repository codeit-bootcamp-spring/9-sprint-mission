package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    public ChannelResponseDto createPublic(
            @RequestBody ChannelCreateRequestDto request
    ) {
        return channelService.createPublic(request);
    }

    @PostMapping("/private")
    public ChannelResponseDto createPrivate(
            @RequestBody ChannelCreateRequestDto request
    ) {
        return channelService.createPrivate(request);
    }

    @GetMapping("/{channelId}")
    public ChannelResponseDto find(
            @PathVariable UUID channelId
    ) {
        return channelService.find(channelId);
    }

    @GetMapping
    public List<ChannelResponseDto> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return channelService.findAllByUserId(userId);
    }

    @PutMapping("/{channelId}")
    public ChannelResponseDto update(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequestDto request
    ) {
        return channelService.update(channelId, request);
    }

    @DeleteMapping("/{channelId}")
    public void delete(
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);
    }
}
