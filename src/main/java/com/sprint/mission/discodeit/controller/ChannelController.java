package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ChannelDto createPublic(@RequestBody PublicChannelCreateRequest request) {
        Channel created = channelService.create(request);
        return channelService.find(created.getId());
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ChannelDto createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        Channel created = channelService.create(request);
        return channelService.find(created.getId());
    }

    // (요구사항) 공개 채널 정보 수정
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public ChannelDto updatePublic(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        Channel channel = channelService.update(channelId, request);
        return channelService.find(channel.getId());
    }

    // 채널 삭제
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }

    // (디버깅/편의) 채널 단건 조회
    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ChannelDto find(@PathVariable UUID channelId) {
        return channelService.find(channelId);
    }
}
