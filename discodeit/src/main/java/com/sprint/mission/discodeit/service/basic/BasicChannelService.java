package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private ChannelView toView(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId()).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        // PRIVATE 참여 유저 (ReadStatus를 채널 기준 한 번에 조회)
        List<UUID> participantUserIds = List.of();
        if (channel.getType() == ChannelType.PRIVATE) {
            participantUserIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(ReadStatus::getUserId)
                    .distinct()
                    .toList();
        }

        return new ChannelView(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getOwnerId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                lastMessageAt,
                participantUserIds
        );
    }

    @Override
    public ChannelView createPublic(PublicChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("channel name must not be blank");
        }
        if (request.ownerId() == null) {
            throw new IllegalArgumentException("ownerId must not be null");
        }

        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.ownerId(), request.description());
        Channel saved = channelRepository.save(channel);
        return toView(saved);
    }

    @Override
    public ChannelView createPrivate(PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.ownerId() == null) {
            throw new IllegalArgumentException("ownerId must not be null");
        }
        if (request.participantUserIds() == null || request.participantUserIds().isEmpty()) {
            throw new IllegalArgumentException("participantUserIds must not be empty");
        }

        Channel channel = new Channel(ChannelType.PRIVATE, null, request.ownerId(), null);
        Channel saved = channelRepository.save(channel);

        List<UUID> memberIds = new java.util.ArrayList<>(request.participantUserIds());
        if (!memberIds.contains(request.ownerId())) {
            memberIds.add(request.ownerId());
        }

        Instant now = Instant.now();
        for (UUID userId : memberIds) {
            // (userId, channelId) 중복 생성 방지
            if (readStatusRepository.findByUserIdAndChannelId(userId, saved.getId()).isPresent()) {
                continue;
            }
            ReadStatus readStatus = new ReadStatus(UUID.randomUUID(), userId, saved.getId(), now);
            readStatusRepository.save(readStatus);
        }

        return toView(saved);
    }

    @Override
    public ChannelView update(ChannelUpdateRequest request) {
        if (request == null || request.channelId() == null) {
            throw new IllegalArgumentException("channelId must not be null");
        }

        UUID channelId = request.channelId();
        String name = request.params() == null ? null : request.params().name();
        String description = request.params() == null ? null : request.params().description();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found. id=" + channelId));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE channel cannot be updated");
        }

        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("channel name must not be blank");
        }

        channel.update(description, name);
        Channel saved = channelRepository.save(channel);
        return toView(saved);
    }

    @Override
    public ChannelView findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        return toView(channel);
    }


    @Override
    public List<ChannelView> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        return channelRepository.findAll().stream()
                .filter(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return true;
                    }

                    return readStatusRepository.findByUserIdAndChannelId(userId, channel.getId()).isPresent();
                })
                .map(this::toView)
                .toList();
    }

    @Override
    public void delete(ChannelDeleteRequest request) {
        if (request == null || request.channelId() == null) {
            throw new IllegalArgumentException("channelId must not be null");
        }

        UUID channelId = request.channelId();

        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel not found. id=" + channelId);
        }

        for (Message m : messageRepository.findAllByChannelId(channelId)) {
            messageRepository.delete(m.getId());
        }

        readStatusRepository.findAllByChannelId(channelId)
                .forEach(rs -> readStatusRepository.delete(rs.getId()));

        channelRepository.delete(channelId);
    }

    @Override
    public boolean existsById(UUID channelId) {
        return channelRepository.existsById(channelId);
    }

}