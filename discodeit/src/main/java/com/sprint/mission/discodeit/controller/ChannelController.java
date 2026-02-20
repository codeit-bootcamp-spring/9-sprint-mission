package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.ChannelApi;
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
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @Override
  @PostMapping(
      path = "/public",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<Channel> createPublic(
      @RequestPart("publicChannelCreateRequest") PublicChannelCreateRequest publicChannelCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Channel createdPublicChannel = channelService.create(publicChannelCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(createdPublicChannel);
  }

  @Override
  @PostMapping(
      path = "/private",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<Channel> createPrivate(
      @RequestPart("privateChannelCreateRequest") PrivateChannelCreateRequest privateChannelCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Channel createdPrivateChannel = channelService.create(privateChannelCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(createdPrivateChannel);
  }

  @PatchMapping(
      path = "/update/{channelId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<Channel> update(
      @PathVariable("channelId") UUID channelId,
      @RequestPart("channelUpdateRequest") PublicChannelUpdateRequest publicChannelUpdateRequest
  ) {
    Channel updatedChannel = channelService.update(channelId, publicChannelUpdateRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedChannel);
  }

  @Override
  @DeleteMapping(
      path = "/delete/{channelId}"
  )
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Override
  @GetMapping(
      path = "/user/{userId}"
  )
  public ResponseEntity<List<ChannelDto>> findAllByUserId(
      @PathVariable("userId") UUID userId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channelService.findAllByUserId(userId));
  }
}
