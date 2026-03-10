package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Message> messagePage = messageRepository.findAllByChannelId(channel.getId(), pageable);

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