package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<Channel> create(
        @RequestBody ChannelCreateRequest request){
        Channel createdChannel = channelService.create(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdChannel);
    }

    @GetMapping(path = "{channelId}")
    public ResponseEntity<ChannelDto> find(
        @PathVariable("channelId") UUID channelId
    ) {
        ChannelDto channelDto = channelService.find(channelId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channelDto);
    }

    @PatchMapping(
        path = "{channelId}"
    )
    public ResponseEntity<ChannelDto> update(
        @PathVariable("channelId") UUID channelId,
        @RequestBody ChannelUpdateRequest request
    ) {
        ChannelDto updatedChannel = channelService.update(channelId, request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedChannel);
    }

    @DeleteMapping(path = "{channelId}")
    public ResponseEntity<Void> delete(
        @PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAll() {
        List<ChannelDto> channels = channelService.findAll();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channels);
    }
}