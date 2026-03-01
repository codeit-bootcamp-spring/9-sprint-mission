package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {

  private final ChannelService channelService;
  private final MessageRepository messageRepository;

  @Override
  public ResponseEntity<ChannelDto> createPublic(
      @Valid @RequestBody PublicChannelCreateRequest request,
      HttpSession session) {

    UUID currentUserId = (UUID) session.getAttribute("USER_ID");

    if (currentUserId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    Channel channel = channelService.createPublicChannel(request, currentUserId);
    return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(channel));
  }

  @Override
  public ResponseEntity<ChannelDto> createPrivate(
      @Valid @RequestBody PrivateChannelCreateRequest request,
      HttpSession session) {

    UUID currentUserId = (UUID) session.getAttribute("USER_ID");

    if (currentUserId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Channel channel = channelService.createPrivateChannel(request, currentUserId);
    return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(channel));
  }

  @Override
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId) {
    List<ChannelDto> channelDtos = channelService.findAllByUserId(userId).stream()
        .map(this::convertToDto)
        .toList();
    return ResponseEntity.ok(channelDtos);
  }

  @Override
  public ResponseEntity<ChannelDto> update(
      @PathVariable UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request) {
    Channel updated = channelService.update(channelId, request);
    return ResponseEntity.ok(convertToDto(updated));
  }

  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    if (channelService.delete(channelId)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  @Override
  public ResponseEntity<Void> addParticipant(
      @PathVariable UUID channelId,
      @RequestBody ChannelParticipantRequest request) {
    channelService.addParticipant(channelId, request.userId());
    return ResponseEntity.ok().build();
  }

  private ChannelDto convertToDto(Channel channel) {
    Instant lastAt = messageRepository.findLatestByChannelId(channel.getId())
        .map(Message::getCreatedAt)
        .orElse(null);

    return new ChannelDto(
        channel.getId(),
        channel.getType().name(),
        channel.getName(),
        channel.getDescription(),
        channel.getParticipantIds(),
        lastAt
    );
  }
}