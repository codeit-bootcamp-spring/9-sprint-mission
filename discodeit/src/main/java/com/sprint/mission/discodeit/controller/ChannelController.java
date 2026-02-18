package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/channels/public", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelCreateRequest request){
        return ResponseEntity.ok(channelService.create(request));
    }
    @RequestMapping(value = "/channels/private", method = RequestMethod.POST)
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request){
        return ResponseEntity.ok(channelService.create(request));
    }
    @RequestMapping(value = "/channels/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Channel> updateChannel(@PathVariable UUID id, @RequestBody PublicChannelUpdateRequest request) {
        return ResponseEntity.ok(channelService.update(id, request));
    }
    @RequestMapping(value = "/channels/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId){
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

}
