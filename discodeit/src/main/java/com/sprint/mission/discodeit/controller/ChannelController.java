package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Channel API")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "public 채널 생성")
  @PostMapping("/public")
  public ResponseEntity<ChannelDto> publicCreate(
      @Parameter(description = "채널 정보")
      @Valid @RequestBody PublicChannelCreateRequest dto) {

    log.info("public 채널 생성 요청 - 채널 정보: {}", dto);

    Channel channel = channelService.create(dto);
    ChannelDto result = channelService.find(channel.getId());

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(result.id())
        .toUri();

    return ResponseEntity.created(location).body(result);
  }

  @Operation(summary = "private 채널 생성")
  @PostMapping("/private")
  public ResponseEntity<ChannelDto> privateCreate(
      @Parameter(description = "채널 정보")
      @Valid @RequestBody PrivateChannelCreateRequest dto) {

    log.info("private 채널 생성 요청 - 채널 정보: {}", dto);

    Channel channel = channelService.create(dto);
    ChannelDto result = channelService.find(channel.getId());

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(result.id())
        .toUri();
    return ResponseEntity.created(location).body(result);
  }

  @Operation(summary = "public 채널 수정")
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelDto> edit(
      @Parameter(description = "채널 ID")
      @Valid @PathVariable UUID channelId,
      @Parameter(description = "채널 정보")
      @Valid @RequestBody PublicChannelUpdateRequest dto
  ) {

    log.info("public 채널 수정 요청 - 채널 ID: {}, 채널 정보: {}", channelId, dto);

    channelService.update(channelId, dto);
    ChannelDto result = channelService.find(channelId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "채널 삭제")
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "채널 ID")
      @Valid @PathVariable UUID channelId
  ) {
    log.info("채널 삭제 요청 - 채널 ID: {}", channelId);

    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "유저 소속 채널 출력")
  @GetMapping("")
  public ResponseEntity<List<ChannelDto>> channelList(
      @Parameter(description = "유저 ID")
      @Valid @RequestParam UUID userId
  ) {
    List<ChannelDto> channelList = channelService.findAllByUserId(userId);
    return ResponseEntity.ok(channelList);
  }
}
