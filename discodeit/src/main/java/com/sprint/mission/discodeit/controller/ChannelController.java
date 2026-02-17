package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // 1) 공개 채널 생성
    // POST /channels/public
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public Channel createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        return channelService.create(request);
    }

    // 2) 비공개 채널 생성
    // POST /channels/private
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public Channel createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.create(request);
    }

    // 3) 채널 정보 수정 (공개 채널)
    // PUT /channels/{channelId}
    @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
    public Channel updatePublicChannel(@PathVariable UUID channelId,
                                       @RequestBody PublicChannelUpdateRequest request) {
        return channelService.update(channelId, request);
    }

    // 4) 채널 삭제
    // DELETE /channels/{channelId}
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }

    // 5) 특정 사용자가 볼 수 있는 모든 채널 목록 조회
    // GET /channels?userId=...
    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> getChannelsByUser(@RequestParam UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    // (선택) 채널 단건 조회 - service에 find가 있으니 테스트에 유리
    // GET /channels/{channelId}
    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ChannelDto getChannel(@PathVariable UUID channelId) {
        return channelService.find(channelId);
    }
}



