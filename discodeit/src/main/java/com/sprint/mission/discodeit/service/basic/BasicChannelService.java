package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jpa.ChannelJpaRepository;
import com.sprint.mission.discodeit.repository.jpa.MessageJpaRepository;
import com.sprint.mission.discodeit.repository.jpa.ReadStatusJpaRepository;
import com.sprint.mission.discodeit.repository.jpa.UserJpaRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelJpaRepository channelRepository;
    private final ReadStatusJpaRepository readStatusRepository;
    private final MessageJpaRepository messageRepository;
    private final UserJpaRepository userRepository; // User 엔티티 조회용

    @Override
    public Channel create(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().forEach(userId -> {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
            ReadStatus readStatus = new ReadStatus(user, createdChannel, Instant.MIN);
            readStatusRepository.save(readStatus);
        });

        return createdChannel;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));
        return toDto(channel);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> subscribedIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(rs -> rs.getChannel().getId())
            .collect(Collectors.toList());

        return channelRepository.findAll().stream()
            .filter(
                c -> c.getType().equals(ChannelType.PUBLIC) || subscribedIds.contains(c.getId()))
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.update(request.newName(), request.newDescription());
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());
        channelRepository.deleteById(channelId);
    }

    private ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId()).stream()
            .map(Message::getCreatedAt)
            .max(Instant::compareTo)
            .orElse(Instant.MIN);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            participantIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(rs -> rs.getUser().getId())
                .collect(Collectors.toList());
        }

        return new ChannelDto(
            channel.getId(),
            channel.getCreatedAt(),
            channel.getUpdatedAt(),
            channel.getType().name(),
            channel.getName(),
            channel.getDescription()
        );
    }
}
