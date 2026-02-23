package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublic(@RequestBody PublicChannelCreateRequest request){
        ChannelDto response = channelService.createPublicChannel(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivate(@RequestBody PrivateChannelCreateRequest request){
        ChannelDto response = channelService.createPrivateChannel(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(@PathVariable UUID channelId
        , @RequestBody ChannelUpdateRequest request){
        ChannelDto response = channelService.update(channelId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId){

        channelService.remove(channelId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> findByUserId(@RequestParam(value = "userId")  UUID userId){

        List<ChannelDto> list = channelService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }
}

