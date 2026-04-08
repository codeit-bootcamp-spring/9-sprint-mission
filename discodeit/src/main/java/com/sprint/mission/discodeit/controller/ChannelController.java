package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @Override
  public ResponseEntity<ChannelDto> createPublic(PublicChannelCreateRequest request,
      HttpSession session) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPublicChannel(request, getSessionUserId(session)));
  }

  @Override
  public ResponseEntity<ChannelDto> createPrivate(PrivateChannelCreateRequest request,
      HttpSession session) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPrivateChannel(request, getSessionUserId(session)));
  }

  @Override
  public ResponseEntity<List<ChannelDto>> findAll(UUID userId, HttpSession session) {
    UUID currentUserId = getSessionUserId(session);
    if (!currentUserId.equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한 없음");
    }
    return ResponseEntity.ok(channelService.findAllByUserId(currentUserId));
  }

  @Override
  public ResponseEntity<ChannelDto> update(UUID channelId, PublicChannelUpdateRequest request,
      HttpSession session) {
    return ResponseEntity.ok(channelService.update(channelId, request, getSessionUserId(session)));
  }

  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID channelId, HttpSession session) {

    UUID currentUserId = getSessionUserId(session);

    log.info("Deleting channel: {} by user: {}", channelId, currentUserId);

    channelService.delete(channelId, currentUserId);

    return ResponseEntity.noContent().build();
  }

  private UUID getSessionUserId(HttpSession session) {
    UUID userId = (UUID) session.getAttribute("USER_ID");
    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 필요");
    }
    return userId;
  }
}