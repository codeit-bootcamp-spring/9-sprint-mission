package com.sprint.mission.discodeit.controller.channel;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import org.springframework.web.bind.annotation.RestController;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
public class CreateChannelController {

    private final ChannelService channelService;

    @RequestMapping( value = "/create", method = RequestMethod.POST, path = "/public")
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
}
