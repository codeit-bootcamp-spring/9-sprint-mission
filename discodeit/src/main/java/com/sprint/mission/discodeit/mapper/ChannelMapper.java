package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    public ChannelDto toDto(Channel channel) {
        // 채널 목록에서 보여줄 '마지막 메시지 시간'을 찾기 위해 최신 메시지 1개를 조회합니다.
        Pageable pageable = PageRequest.of(0, 1);

        // [수정됨] 레포지토리 메서드 규격에 맞춰 두 번째 인자에 null(커서 없음)을 추가합니다.
        Slice<Message> messagePage = messageRepository.findAllByChannelIdOrderByCreatedAtDesc(channel.getId(), pageable);

        Instant lastMessageAt = messagePage.getContent().stream()
                .findFirst()
                .map(Message::getCreatedAt)
                .orElse(channel.getCreatedAt());

        List<UUID> participantIds = readStatusRepository.findAllByChannelId(channel.getId())
                .stream()
                .map(readStatus -> readStatus.getUser().getId())
                .toList();

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participantIds,
                lastMessageAt
        );
    }
}