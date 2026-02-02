package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.ChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public Channel create(@RequestBody ChannelCreateRequestDto requestDto) {
        return channelService.create(
                requestDto.type(),
                requestDto.name(),
                requestDto.description()
        );
    }

    @GetMapping("/{channelId}")
    public Channel find(@PathVariable UUID channelId){
        return channelService.find(channelId);
    }

    @GetMapping
    public List<Channel> findAll() {
        return channelService.findAll();
    }

}
