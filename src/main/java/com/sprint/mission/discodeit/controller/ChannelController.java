package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 채널 API 컨트롤러.
 * PUBLIC/PRIVATE 채널 생성, 수정, 삭제, 목록 조회를 처리한다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping(path = "public")
  @Override
  public ResponseEntity<ChannelDto> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request) {
    ChannelDto createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @PostMapping(path = "private")
  @Override
  public ResponseEntity<ChannelDto> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request) {
    ChannelDto createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @PatchMapping(path = "{channelId}")
  @Override
  public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request) {
    ChannelDto updatedChannel = channelService.update(channelId, request);
    return ResponseEntity.ok(updatedChannel);
  }

  @DeleteMapping(path = "{channelId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  /** 사용자가 접근 가능한 채널 목록 조회 (PUBLIC + 참여 중인 PRIVATE) */
  @GetMapping
  @Override
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }
}
