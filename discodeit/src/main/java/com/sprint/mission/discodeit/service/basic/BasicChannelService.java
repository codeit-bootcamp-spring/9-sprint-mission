package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
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
    public Optional<ChannelDto.Response> createPublicChannel(ChannelDto.CreatePublicRequest request) {
        Channel channel = new Channel(request.name(), request.type(), request.description(), false);
        channelRepository.save(channel);
        return Optional.of(convertToResponse(channel));
    }

    @Override
    public Optional<ChannelDto.Response> createPrivateChannel(ChannelDto.CreatePrivateRequest request) {
        Channel channel = new Channel(request.name(), request.type(), request.description(), true);
        channel.setParticipantUserIds(request.participantUserIds());
        channelRepository.save(channel);

        for (UUID userId : request.participantUserIds()) {
            readStatusRepository.save(new ReadStatus(userId, channel.getId()));
        }
        return Optional.of(convertToResponse(channel));
    }

    @Override
    public List<ChannelDto.Response> findAll() {
        List<Channel> channels = channelRepository.findAll();
        List<ChannelDto.Response> responses = new ArrayList<>();
        for (Channel channel : channels) {
            responses.add(convertToResponse(channel));
        }
        return responses;
    }

    @Override
    public List<ChannelDto.Response> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAllByUserId(userId);
        List<ChannelDto.Response> responses = new ArrayList<>();
        for (Channel channel : channels) {
            responses.add(convertToResponse(channel));
        }
        return responses;
    }

    @Override
    public Optional<ChannelDto.Response> findById(UUID id) {
        return channelRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public Optional<ChannelDto.Response> update(UUID id, String name, String description) {
        return channelRepository.findById(id).map(channel -> {
            channel.updateInfo(name, description);
            channel.recordUpdate();
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

    private ChannelDto.Response convertToResponse(Channel channel) {
        Instant lastAt = messageRepository.findLatestByChannelId(channel.getId())
                .map(Message::getCreatedAt).orElse(null);
        return new ChannelDto.Response(channel.getId(), channel.getName(), channel.getDescription(),
                channel.getType().name(), lastAt, channel.getParticipantUserIds());
    }
}