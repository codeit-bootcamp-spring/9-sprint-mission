package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    // ================= 기존 CRUD =================
    @Override
    public void create(Channel channel) {
        channelRepository.create(channel);
    }

    @Override
    public Channel findByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public boolean update(UUID id, String name, String description, boolean isPrivate) {
        Channel channel = channelRepository.findById(id);
        if (channel == null || channel.isPrivate()) return false;
        return channelRepository.update(id, name, description, isPrivate);
    }

    @Override
    public boolean delete(UUID id) {
        return channelRepository.delete(id);
    }

    // ================= DTO 기반 신규 기능 =================

    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        Channel channel = new Channel(
                request.name(),
                request.description(),
                request.isPrivate()
        );
        channelRepository.create(channel);

        if (request.isPrivate()) {
            if (request.participantUserIds() == null || request.participantUserIds().isEmpty()) {
                throw new IllegalArgumentException("PRIVATE 채널은 참여자가 필요합니다.");
            }

            for (UUID userId : request.participantUserIds()) {
                if (userRepository.findById(userId) == null) {
                    throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
                }

                ReadStatus status = new ReadStatus(userId, channel.getId(), Instant.now());
                readStatusRepository.create(status);
            }
        }

        return toResponse(channel);
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) return null;
        return toResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAllDto() {
        return channelRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.findAll()
                .stream()
                .filter(ch ->
                        !ch.isPrivate() ||
                                readStatusRepository.findAllByUserId(userId)
                                        .stream()
                                        .anyMatch(rs -> rs.getChannelId().equals(ch.getId()))
                )
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ================= 내부 변환 =================
    private ChannelResponse toResponse(Channel channel) {
        List<Message> messages = messageRepository.findByChannelId(channel.getId());

        Instant lastMessageAt = messages.isEmpty()
                ? null
                : Instant.ofEpochMilli(messages.get(messages.size() - 1).getCreatedAt());

        List<UUID> participants = channel.isPrivate()
                ? readStatusRepository.findAllByChannelId(channel.getId())
                .stream()
                .map(ReadStatus::getUserId)
                .toList()
                : null;

        return new ChannelResponse(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelDescription(),
                channel.isPrivate(),
                participants,
                lastMessageAt
        );
    }
}


