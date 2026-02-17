package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelResponse createPublicChannel(@RequestBody CreatePublicChannelRequest request){
        return channelService.createPublic(request);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelResponse createPrivateChannel(@RequestBody CreatePrivateChannelRequest request){
        return channelService.createPrivate(request);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ChannelResponse findById (@PathVariable UUID channelId){
        return channelService.findById(channelId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
    public ChannelResponse updateChannel(
            @PathVariable UUID channelId,
            @RequestBody UpdateChannelRequest request){
        return channelService.update(channelId, request);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID channelId){
        channelService.delete(channelId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelResponse> findAllChannelsByUserId(@RequestParam UUID userId){
        return channelService.findAllByUserId(userId);
    }


}
