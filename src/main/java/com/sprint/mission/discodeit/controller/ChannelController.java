package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/public")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPublicChannel(
        @RequestBody PublicChannelCreateRequest request
    ) {
        return channelService.createPublic(request);
    }

    @PostMapping("/private")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createPrivateChannel(
        @RequestBody PrivateChannelCreateRequest request
    ) {
        return channelService.createPrivate(request);
    }

    @GetMapping("/{channelId}")
    public ChannelDto findById(@PathVariable UUID channelId) {
        return channelService.findById(channelId);
    }

    @PatchMapping("/{channelId}")
    public ChannelDto updateChannel(
        @PathVariable UUID channelId,
        @RequestBody ChannelUpdateRequest request
    ) {
        return channelService.update(channelId, request);
    }

    @DeleteMapping("/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }

    @GetMapping
    public List<ChannelDto> findAllChannelsByUserId(
        @RequestParam UUID userId
    ) {
        return channelService.findAllByUserId(userId);
    }
}