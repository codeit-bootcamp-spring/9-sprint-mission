package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(
            path = "/create/public",
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Channel> createPublic(
            @RequestPart("publicChannelCreateRequest") PublicChannelCreateRequest publicChannelCreateRequest
            ) {
        Channel createdPublicChannel = channelService.create(publicChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPublicChannel);
    }

    @RequestMapping(
            path = "/create/private",
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Channel> creatPrivate(
            @RequestPart("privateChannelCreateRequest") PrivateChannelCreateRequest privateChannelCreateRequest
    ) {
        Channel createdPrivateChannel = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPrivateChannel);
    }

    @RequestMapping(
            path = "/update/{channelId}",
            method = RequestMethod.PUT,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<Channel> update(
            @PathVariable UUID channelId,
            @RequestPart("channelUpdateRequest") PublicChannelUpdateRequest publicChannelUpdateRequest
            ) {
        Channel updatedChannel = channelService.update(channelId, publicChannelUpdateRequest);
        return ResponseEntity.ok(updatedChannel);
    }

    @RequestMapping(
            path = "/delete/{channelId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            path = "/user/{userId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@PathVariable UUID userId)
    {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}
