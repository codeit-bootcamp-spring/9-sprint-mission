package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public Optional<ChannelResponse> createChannel(ChannelCreateRequest request) {
        Channel channel = new Channel(request.name(), ChannelType.valueOf(request.type()), request.description(), request.isPrivate());
        if (request.isPrivate() && request.participantUserIds() != null) {
            channel.setParticipantUserIds(request.participantUserIds());
            channelRepository.save(channel);
            for (UUID userId : request.participantUserIds()) {
                readStatusRepository.save(new ReadStatus(userId, channel.getId()));
            }
        } else {
            channelRepository.save(channel);
        }
        return Optional.of(convertToResponse(channel));
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(c -> !c.isPrivate() || (c.getParticipantUserIds() != null && c.getParticipantUserIds().contains(userId)))
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public Optional<ChannelResponse> findById(UUID id) {
        return channelRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public Optional<ChannelResponse> update(UUID id, String name, String description) {
        return channelRepository.findById(id).map(channel -> {
            channel.update(name, description);
            channelRepository.save(channel);
            return convertToResponse(channel);
        });
    }

    @Override
    public boolean delete(UUID id) {
        if (channelRepository.findById(id).isPresent()) {
            channelRepository.delete(id);
            return true;
        }
        return false;
    }

    private ChannelResponse convertToResponse(Channel channel) {
        Instant lastAt = messageRepository.findLatestByChannelId(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType().name(),
                lastAt,
                channel.getParticipantUserIds(),
                channel.isPrivate()
        );
    }
}