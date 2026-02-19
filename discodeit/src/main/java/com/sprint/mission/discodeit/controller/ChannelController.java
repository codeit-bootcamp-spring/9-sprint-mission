package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/channel")
public class ChannelController {
    private final ChannelService channelService;
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }
    //공개
    @RequestMapping(value = "/public",method = RequestMethod.POST)
    public ResponseEntity<Channel> createChannel(@RequestBody PublicChannelCreateRequest request){
        Channel channel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }
    //비공개
    @RequestMapping(value = "/private",method = RequestMethod.POST)
    public ResponseEntity<Channel> createChannel(@RequestBody PrivateChannelCreateRequest request){
        Channel channel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }
    //수정
    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<Channel> PublicChannelUpdateRequest(@RequestBody PublicChannelUpdateRequest request , @PathVariable UUID id){
        Channel channel = channelService.update(id,request);
        return ResponseEntity.ok(channel);
    }
    //삭제
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(@PathVariable UUID id){
        channelService.delete(id);
    }
    //조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> getChannels(@PathVariable UUID id) {
        List<ChannelDto> channels = channelService.findAllByUserId(id);
        return ResponseEntity.ok(channels);
    }

}
