package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import org.springframework.web.bind.annotation.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST, path = "/public")
    public Channel createPublicChannel(
            @RequestBody PublicChannelCreateRequest request
            ) {
        return channelService.create(request);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/private")
    public Channel createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        return channelService.create(request);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return channelService.findAllByUserId(userId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{channelId}")
    public ChannelDto findById(
            @PathVariable UUID channelId
    ) {
        return channelService.find(channelId);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{channelId}")
    public Channel updatePublicChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        return channelService.update(channelId, request);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{channelId}")
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }
}
