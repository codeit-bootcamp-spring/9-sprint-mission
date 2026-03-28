package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RequestChannelDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final UserService userService;

  @RequestMapping(value = "public", method = RequestMethod.POST)
  public ResponseChannelDto handleCreatePublicChannel(
      @RequestBody RequestChannelDto channelDto
  ) {
    channelDto.setType(ChannelType.PUBLIC);
    return channelService.create(channelDto);
  }

  @RequestMapping(value = "private", method = RequestMethod.POST)
  public ResponseChannelDto handleCreatePrivateChannel(
      @RequestBody RequestChannelDto channelDto
  ) {
    channelDto.setType(ChannelType.PRIVATE);
    return channelService.create(channelDto);
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<ResponseChannelDto> findChannel(
      @RequestParam("id") UUID userId
  ) {
    return channelService.findAll(userId);
  }

  @RequestMapping(value = "{channelId}", method = RequestMethod.PATCH)
  public ResponseChannelDto handleUpdateChannel(
      @PathVariable UUID channelId,
      @RequestBody RequestUpdateChannelDto updateChannelDto
  ) {
    return channelService.update(channelId, updateChannelDto);
  }

  @RequestMapping(value = "{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<String> handleDeleteChannel(
      @PathVariable UUID channelId,
      @RequestParam("userId") UUID userId
  ) {
    if (!channelService.isPresent(channelId)) {
      throw new NotFound("The channel does not exist.");
    }

    channelService.delete(channelId);
    return new ResponseEntity<>("Success: " + channelId + " has been deleted.", HttpStatus.OK);
  }
}