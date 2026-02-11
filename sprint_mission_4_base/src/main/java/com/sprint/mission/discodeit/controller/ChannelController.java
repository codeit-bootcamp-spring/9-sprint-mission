package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;

    /**
     * ✅ 공개 채널 생성
     */
    @PostMapping("/public")
    public Channel createPublicChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        return channelService.create(request);
    }

    /**
     * ✅ 비공개 채널 생성
     */
    @PostMapping("/private")
    public Channel createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        return channelService.create(request);
    }

    /**
     * ✅ 채널 단건 조회
     */
    @GetMapping("/{channelId}")
    public ChannelDto findChannel(
            @PathVariable UUID channelId
    ) {
        return channelService.find(channelId);
    }

    /**
     * ✅ 특정 사용자가 볼 수 있는 모든 채널 조회
     */
    @GetMapping
    public List<ChannelDto> findAllByUserId(
            @RequestParam UUID userId
    ) {
        return channelService.findAllByUserId(userId);
    }

    /**
     * ✅ 공개 채널 정보 수정
     */
    @PatchMapping("/{channelId}")
    public Channel updatePublicChannel(
            @PathVariable UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {
        return channelService.update(channelId, request);
    }

    /**
     * ✅ 채널 삭제
     */
    @DeleteMapping("/{channelId}")
    public void deleteChannel(
            @PathVariable UUID channelId
    ) {
        channelService.delete(channelId);
    }
}
