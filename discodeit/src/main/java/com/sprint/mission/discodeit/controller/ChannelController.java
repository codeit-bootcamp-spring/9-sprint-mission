package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping(path = "/public")
  public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);

    URI location = buildChannelLocation(createdChannel.getId());

    return ResponseEntity
        .created(location)
        .body(createdChannel);
  }

  @PostMapping(path = "/private")
  public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);

    URI location = buildChannelLocation(createdChannel.getId());

    return ResponseEntity
        .created(location)
        .body(createdChannel);
  }

  @PatchMapping(path = "{channelId}")
  public ResponseEntity<Channel> update(@PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel udpatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(udpatedChannel);
  }

  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }

  private URI buildChannelLocation(UUID channelId) {
    return ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/api/channels/{id}")
        .buildAndExpand(channelId)
        .toUri();
  }
}
