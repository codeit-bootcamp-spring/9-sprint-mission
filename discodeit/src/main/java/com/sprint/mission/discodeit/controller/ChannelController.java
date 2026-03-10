package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @Override
  public ResponseEntity<ChannelDto> createPublic(PublicChannelCreateRequest request,
      HttpSession session) {
    UUID currentUserId = getSessionUserId(session);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPublicChannel(request, currentUserId));
  }

  @Override
  public ResponseEntity<ChannelDto> createPrivate(PrivateChannelCreateRequest request,
      HttpSession session) {
    UUID currentUserId = getSessionUserId(session);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelService.createPrivateChannel(request, currentUserId));
  }

  /**
   * [수정 반영] GET /api/channels/private 요청 처리 프론트엔드가 생성 후 해당 경로로 데이터를 읽으러 올 때 405 에러가 나지 않도록 합니다.
   */
  // ChannelController.java 수정
  @Override
  public ResponseEntity<List<ChannelDto>> findPrivateChannels(HttpSession session) {
    UUID currentUserId = getSessionUserId(session);
    // [수정] 모든 채널이 아니라 PRIVATE만 필터링 (C++의 필터링 이터레이터 처럼)
    List<ChannelDto> privateChannels = channelService.findAllByUserId(currentUserId).stream()
        .filter(c -> "PRIVATE".equals(c.type()))
        .toList();
    return ResponseEntity.ok(privateChannels);
  }

  @Override
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId, HttpSession session) {
    UUID currentUserId = getSessionUserId(session);

    if (!currentUserId.equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "타인의 채널 목록을 조회할 권한이 없습니다.");
    }

    return ResponseEntity.ok(channelService.findAllByUserId(currentUserId));
  }

  @Override
  public ResponseEntity<ChannelDto> update(UUID channelId, PublicChannelUpdateRequest request) {
    return ResponseEntity.ok(channelService.update(channelId, request));
  }

  @Override
  public ResponseEntity<Void> delete(UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  private UUID getSessionUserId(HttpSession session) {
    UUID userId = (UUID) session.getAttribute("USER_ID");
    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다.");
    }
    return userId;
  }
}