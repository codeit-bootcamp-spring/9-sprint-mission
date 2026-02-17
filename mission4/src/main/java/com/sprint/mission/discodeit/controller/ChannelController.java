package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(
            path="/public",
            method = RequestMethod.POST
    )
    public ResponseEntity<Channel> createPublic(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest
            ){
       Channel publicChannel= channelService.create(publicChannelCreateRequest);
        return ResponseEntity.ok(publicChannel);
    }
    @RequestMapping(
            path="/private",
            method = RequestMethod.POST
    )
    public ResponseEntity<Channel> createPrivate(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest
            ){
        Channel privateChannel = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.ok(privateChannel);
    }

    @RequestMapping(

            method = RequestMethod.GET
    )
    public ResponseEntity<ChannelDto> find(
            @RequestParam UUID channelId
            ){
        ChannelDto findChannel = channelService.find(channelId);
        return ResponseEntity.ok(findChannel);
    }
    @RequestMapping(
            path = "/All",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<ChannelDto>> findByUserId(
            @RequestParam UUID userId
    ){
        List<ChannelDto> allChannel = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(allChannel);
    }
    @RequestMapping(

            method = RequestMethod.PUT
    )
    public ResponseEntity<Channel> update(
            @RequestParam UUID channelId,
            @RequestBody PublicChannelUpdateRequest request

    ){
        Channel updateChannel = channelService.update(channelId,request);
        return ResponseEntity.ok(updateChannel);
    }

    @RequestMapping(

            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(
            @RequestParam UUID channelId
    ){
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
}
