package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelDeleteRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelView;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @Override
  public ChannelView createPublic(PublicChannelCreateRequest request) {
    return channelService.createPublic(request);
  }

  @Override
  public ChannelView createPrivate(PrivateChannelCreateRequest request) {
    return channelService.createPrivate(request);
  }

  @Override
  public ChannelView findById(UUID channelId) {
    return channelService.findById(channelId);
  }

  @Override
  public ChannelView update(UUID channelId, ChannelUpdateRequest.ChannelUpdateParams params) {
    return channelService.update(new ChannelUpdateRequest(channelId, params));
  }

  @Override
  public ResponseEntity<Void> delete(UUID channelId) {
    channelService.delete(new ChannelDeleteRequest(channelId));
    return ResponseEntity.noContent().build();
  }

  @Override
  public List<ChannelView> findAllByUserId(UUID userId) {
    return channelService.findAllByUserId(userId);
  }
}