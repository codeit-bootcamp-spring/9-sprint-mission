package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  public ChannelResponse toResponse(Channel channel) {
    if (channel == null) {
      return null;
    }

    // 메모리에서 마지막 메시지 조회 (N+1 문제 방지)
    // Channel의 messages가 이미 로딩되어 있다면 사용
    Instant lastMessageAt = channel.getMessages().stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(Instant.MIN);

    // fetch join을 사용하여 N+1 문제 해결
    List<UserResponse> participants = readStatusRepository
        .findAllByChannel_IdWithUser(channel.getId())
        .stream()
        .map(ReadStatus::getUser)
        .map(userMapper::toResponse)
        .toList();

    return new ChannelResponse(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );
  }
}
