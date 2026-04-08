package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping(path = "public")
  public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
    log.info("공개 채널 생성 요청 들어옴! 채널명: {}", request.name());
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @PostMapping(path = "private")
  public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
    log.info("비공개 채널 생성 요청 들어옴! 참여자 수: {}명", request.participantIds().size());
    Channel createdChannel = channelService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @PatchMapping(path = "{channelId}")
  public ResponseEntity<Channel> update(@PathVariable("channelId") UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    log.info("채널 수정 요청 들어옴! 대상 채널 ID: {}", channelId);
    Channel udpatedChannel = channelService.update(channelId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(udpatedChannel);
  }

  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {
    log.info("채널 삭제 요청 들어옴! 대상 채널 ID: {}", channelId);
    channelService.delete(channelId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @Override
  @GetMapping
  public ResponseEntity<PageResponse<ChannelDto>> findAll(
      @RequestParam("userId") UUID userId,
      @RequestParam(value = "page", defaultValue = "0") int page // 💡 page 번호를 받도록 추가!
  ) {
    // 서비스에서 50개씩 가져오는 페이징 메서드 호출
    PageResponse<ChannelDto> channels = channelService.findAll(userId, page);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
