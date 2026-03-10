package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping(path = "public")
    public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PostMapping(path = "private")
    public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdChannel);
    }

    @PatchMapping(path = "{channelId}")
    public ResponseEntity<Channel> update(@PathVariable("channelId") UUID channelId,
                                          @RequestBody PublicChannelUpdateRequest request) {
        Channel updatedChannel = channelService.update(channelId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedChannel);
    }

    @DeleteMapping(path = "{channelId}")
    public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<ChannelDto>> findAll(
            @RequestParam("userId") UUID userId,
            // page 파라미터를 추가하며, 기본값을 0으로 설정합니다.
            @RequestParam(value = "page", defaultValue = "0") int page,
            // size 파라미터를 추가하며, 기본값을 10으로 설정합니다.
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageResponse<ChannelDto> channels = channelService.findAllByUserId(userId, page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(channels);
    }
}