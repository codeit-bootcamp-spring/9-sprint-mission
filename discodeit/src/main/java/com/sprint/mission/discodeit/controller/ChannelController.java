package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
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
    public ResponseEntity<ChannelResponse> createPublic(@RequestBody CreatePublicChannelRequest request){
        ChannelResponse response = channelService.createPublicChannel(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponse> createPrivate(@RequestBody CreatePrivateChannelRequest request){
        ChannelResponse response = channelService.createPrivateChannel(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> update(@PathVariable UUID channelId
        , @RequestBody UpdateChannelRequest request){
        ChannelResponse response = channelService.update(channelId, request);
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
    public ResponseEntity<List<ChannelResponse>> findByUserId(@RequestParam(value = "userId")  UUID userId){

        List<ChannelResponse> list = channelService.findAllByUserId(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }
}

