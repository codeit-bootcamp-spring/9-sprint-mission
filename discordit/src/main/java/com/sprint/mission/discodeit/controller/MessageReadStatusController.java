package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RequestCreateReadStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseReadStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class MessageReadStatusController {

  private final ReadStatusService readStatusService;
  private final ChannelService channelService;

  @RequestMapping(method = RequestMethod.POST)
  public List<ResponseReadStatus> handleCreateReadStatus(
      @RequestParam("userId") UUID userId
  ) {
    List<UUID> channelIds = new ArrayList<>();

    for (ResponseChannelDto dto : channelService.findAll(userId)) {
      channelIds.add(dto.channelId());
    }

    return readStatusService.create(new RequestCreateReadStatusDto(userId, channelIds));
  }

  @RequestMapping(method = RequestMethod.PATCH)
  public ResponseReadStatus handleUpdateReadStatus(
      @RequestParam("userId") UUID userId,
      @RequestParam("channelId") UUID channelId
  ) {
    return readStatusService.update(userId, channelId);
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<ResponseReadStatus> handleFindAllReadStatusForUser(
      @RequestParam("userId") UUID userId) {
    return readStatusService.findAllByUserId(userId);
  }
}