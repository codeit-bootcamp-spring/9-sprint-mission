package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final UserService userService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPublic(@RequestBody CreatePublicChannelRequest request){
        ChannelResponse response = channelService.createPublicChannel(request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> createPrivate(@RequestBody CreatePrivateChannelRequest request){
        ChannelResponse response = channelService.createPrivateChannel(request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ChannelResponse> update(@RequestBody UpdateChannelRequest request){
        ChannelResponse response = channelService.update(request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void delete(@RequestParam UUID channelId){
        channelService.remove(channelId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelResponse>> findPublic(@RequestParam UUID userId){
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}

