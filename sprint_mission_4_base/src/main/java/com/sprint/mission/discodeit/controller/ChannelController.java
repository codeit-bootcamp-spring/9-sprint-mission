package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/createPublicChannel")
    public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest publicChannelCreateRequest)
    {
        Channel channel = channelService.create(publicChannelCreateRequest);
        return ResponseEntity.ok(channel);
    }

    @PostMapping("/createPrivateChannel")
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest privateChannelCreateRequest)
    {
        Channel channel = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.ok(channel);
    }

    @PatchMapping("/updateChannel/{channelId}")
    public ResponseEntity<Channel> updateChannel(@PathVariable UUID channelId,
                                                 @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest)
    {
        Channel channel = channelService.update(channelId, publicChannelUpdateRequest);
        return ResponseEntity.ok(channel);
    }

    @DeleteMapping("/deleteChannel/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId)
    {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("findChannel/{channelId}")
    public ResponseEntity<ChannelDto> findChannel(@PathVariable UUID channelId) {
        ChannelDto channels = channelService.find(channelId);
        return ResponseEntity.ok(channels);
    }

}
