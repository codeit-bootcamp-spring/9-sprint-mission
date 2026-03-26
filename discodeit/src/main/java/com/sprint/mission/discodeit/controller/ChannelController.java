package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @Override
  @PostMapping(path = "/public")
  public ResponseEntity<ChannelDto> create(@Valid @RequestBody PublicChannelCreateRequest request) {
    log.debug("POST /api/channels/public - create public channel: name={}", request.name());
    ChannelDto createdChannel = channelService.create(request);

    URI location = buildChannelLocation(createdChannel.id());

    return ResponseEntity
        .created(location)
        .body(createdChannel);
  }

  @Override
  @PostMapping(path = "/private")
  public ResponseEntity<ChannelDto> create(@Valid @RequestBody PrivateChannelCreateRequest request) {
    log.debug("POST /api/channels/private - create private channel: participantCount={}",
        request.participantIds().size());
    ChannelDto createdChannel = channelService.create(request);

    URI location = buildChannelLocation(createdChannel.id());

    return ResponseEntity
        .created(location)
        .body(createdChannel);
  }

  @Override
  @PatchMapping(path = "/{channelId}")
  public ResponseEntity<ChannelDto> update(@PathVariable UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request) {
    log.debug("PATCH /api/channels/{} - update channel", channelId);
    ChannelDto updatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedChannel);
  }

  @Override
  @DeleteMapping(path = "/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    log.debug("DELETE /api/channels/{} - delete channel", channelId);
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Override
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
