package com.sprint.mission.discodeit.controller.channel;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
public class ChannelQueryController {

    private final ChannelService channelService;

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
}

