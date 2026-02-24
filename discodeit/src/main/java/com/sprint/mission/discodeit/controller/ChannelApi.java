package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import jakarta.servlet.http.HttpSession; // 🌟 세션 단자 추가
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/channels")
public interface ChannelApi {

  @PostMapping("/public")
  ResponseEntity<ChannelDto> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request,
      HttpSession session
  );

  @PostMapping("/private")
  ResponseEntity<ChannelDto> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request,
      HttpSession session
  );

  @GetMapping
  ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId);

  @PutMapping("/{channelId}")
  ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request
  );

  @DeleteMapping("/{channelId}")
  ResponseEntity<Void> delete(@PathVariable UUID channelId);

  @PostMapping("/{channelId}/participants")
  ResponseEntity<Void> addParticipant(
      @PathVariable UUID channelId,
      @RequestBody ChannelParticipantRequest request
  );
}