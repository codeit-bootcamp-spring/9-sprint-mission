// src/main/java/com/sprint/mission/discodeit/service/basic/BasicChannelService.java
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    public BasicChannelService(
            ChannelRepository channelRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            ReadStatusRepository readStatusRepository
    ) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.readStatusRepository = readStatusRepository;
    }

    // ===== 기존 CRUD =====
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
        return channelRepository.update(id, name, description, isPrivate);
    }

    @Override
    public boolean delete(UUID id) {
        // 메시지/읽음상태 같이 삭제
        List<Message> msgs = messageRepository.findByChannelId(id);
        for (Message m : msgs) {
            messageRepository.delete(m.getId());
        }
        readStatusRepository.deleteAllByChannelId(id);
        return channelRepository.delete(id);
    }

    // ===== DTO 기능 =====
    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        if (request == null) throw new IllegalArgumentException("request is null");

        // PRIVATE면 참가자 필수
        if (request.isPrivate()) {
            List<UUID> participants = request.participantUserIds();
            if (participants == null || participants.isEmpty()) {
                throw new IllegalArgumentException("PRIVATE 채널은 참가자 목록이 필요합니다.");
            }
            for (UUID userId : participants) {
                if (userRepository.findById(userId) == null) {
                    throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
                }
            }
        }

        Channel channel = new Channel(request.name(), request.description(), request.isPrivate());
        channelRepository.create(channel);

        if (channel.isPrivate()) {
            for (UUID userId : request.participantUserIds()) {
                ReadStatus rs = new ReadStatus(userId, channel.getId(), Instant.now());
                readStatusRepository.create(rs);
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
        List<Channel> channels = channelRepository.findAll();
        List<ChannelResponse> result = new ArrayList<>();
        for (Channel c : channels) {
            result.add(toResponse(c));
        }
        return result;
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (userId == null) return List.of();

        List<Channel> channels = channelRepository.findAll();
        List<ChannelResponse> result = new ArrayList<>();

        for (Channel ch : channels) {
            if (!ch.isPrivate()) {
                result.add(toResponse(ch));
                continue;
            }
            // PRIVATE: ReadStatus에 있는 채널만 노출
            List<ReadStatus> myReadStatuses = readStatusRepository.findAllByUserId(userId);
            boolean joined = false;
            for (ReadStatus rs : myReadStatuses) {
                if (rs.getChannelId().equals(ch.getId())) {
                    joined = true;
                    break;
                }
            }
            if (joined) result.add(toResponse(ch));
        }

        return result;
    }

    private ChannelResponse toResponse(Channel channel) {
        List<Message> messages = messageRepository.findByChannelId(channel.getId());
        Instant lastMessageAt = null;

        if (messages != null && !messages.isEmpty()) {
            Message last = messages.stream()
                    .max(Comparator.comparingLong(Message::getCreatedAt))
                    .orElse(null);
            if (last != null) {
                lastMessageAt = Instant.ofEpochMilli(last.getCreatedAt());
            }
        }

        List<UUID> participants = null;
        if (channel.isPrivate()) {
            participants = readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }

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



