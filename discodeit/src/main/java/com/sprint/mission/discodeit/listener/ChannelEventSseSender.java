package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChannelEventSseSender {

  private final SseService sseService;

  public void sendChannelCreated(Collection<UUID> targetUserIds, ChannelDto channelDto) {
    log.info("채널 생성 발생 -> SSE 실시간 전송 (channels.created)");

    sseService.send(targetUserIds, "channels.created", channelDto);
  }

  public void sendChannelUpdated(Collection<UUID> targetUserIds, ChannelDto channelDto) {
    log.info("채널 정보 수정 발생 -> SSE 실시간 전송 (channels.updated)");

    sseService.send(targetUserIds, "channels.updated", channelDto);
  }

  public void sendChannelDeleted(Collection<UUID> targetUserIds, ChannelDto channelDto) {
    log.info("채널 삭제 발생 -> SSE 실시간 전송 (channels.deleted)");

    sseService.send(targetUserIds, "channels.deleted", channelDto);
  }
}