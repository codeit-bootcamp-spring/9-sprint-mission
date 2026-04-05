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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;

  @PostMapping(path = "public")
  public ResponseEntity<ChannelDto> create(
      @Valid @RequestBody PublicChannelCreateRequest request) {

    log.info("API 호출 - 공개 채널 생성 요청 name: {}", request.name());

    ChannelDto createdChannel = channelService.create(request);

    log.info("API 완료 - 공개 채널 생성 성공 channelId: {}", createdChannel.id());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @PostMapping(path = "private")
  public ResponseEntity<ChannelDto> create(
      @Valid @RequestBody PrivateChannelCreateRequest request) {

    log.info("API 호출 - 비공개 채널 생성 요청 participantCount: {}", request.participantIds().size());

    ChannelDto createdChannel = channelService.create(request);

    log.info("API 완료 - 비공개 채널 생성 성공 channelId: {}", createdChannel.id());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdChannel);
  }

  @PatchMapping(path = "{channelId}")
  public ResponseEntity<ChannelDto> update(@PathVariable("channelId") UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request) {

    log.info("API 호출 - 채널 수정 요청 channelId: {}", channelId);

    ChannelDto updatedChannel = channelService.update(channelId, request);

    log.info("API 완료 - 채널 수정 성공 channelId: {}", channelId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedChannel);
  }

  @DeleteMapping(path = "{channelId}")
  public ResponseEntity<Void> delete(@PathVariable("channelId") UUID channelId) {

    log.info("API 호출 - 채널 삭제 요청 channelId: {}", channelId);

    channelService.delete(channelId);

    log.info("API 완료 - 채널 삭제 성공 channelId: {}", channelId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {

    log.debug("API 호출 - 채널 목록 조회 userId: {}", userId);

    List<ChannelDto> channels = channelService.findAllByUserId(userId);

    log.debug("API 완료 - 채널 목록 조회 count: {}", channels.size());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels);
  }
}
