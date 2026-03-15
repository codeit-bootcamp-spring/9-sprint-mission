package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "채널 관리 API")
public interface ChannelApi {

  @Operation(summary = "Public Channel 생성")
  @PostMapping("/public")
  ResponseEntity<ChannelDto> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request,
      HttpSession session);

  @Operation(summary = "Private Channel 생성")
  @PostMapping("/private")
  ResponseEntity<ChannelDto> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request,
      HttpSession session);

  @Operation(summary = "Private Channel 상세 조회 (프론트엔드 호환용)")
  @GetMapping("/private")
  ResponseEntity<List<ChannelDto>> findPrivateChannels(HttpSession session);

  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @GetMapping
  ResponseEntity<List<ChannelDto>> findAll(
      @RequestParam(name = "userId") UUID userId,
      HttpSession session);

  @Operation(summary = "Channel 정보 수정")
  @PatchMapping("/{channelId}")
  ResponseEntity<ChannelDto> update(
      @PathVariable(name = "channelId") UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request);

  @Operation(summary = "Channel 삭제")
  @DeleteMapping("/{channelId}")
  ResponseEntity<Void> delete(@PathVariable(name = "channelId") UUID channelId);
}