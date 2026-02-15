package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDeleteRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelView;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelView createPublic(@RequestBody PublicChannelCreateRequest request) {
        return channelService.createPublic(request);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelView createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.createPrivate(request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ChannelView update(
            @PathVariable("id") UUID channelId,
            @RequestBody ChannelUpdateRequest.ChannelUpdateParams params
    ) {
        return channelService.update(new ChannelUpdateRequest(channelId, params));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") UUID channelId) {
        channelService.delete(new ChannelDeleteRequest(channelId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelView> findAllByUserId(@RequestParam("userId") UUID userId) {
        return channelService.findAllByUserId(userId);
    }
}