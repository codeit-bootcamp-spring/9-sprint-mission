package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
    private final UserRepository userRepository;

    private ChannelView toView(Channel channel) {
        // 1) 최근 메시지 시간
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId()).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        // 2) PRIVATE 참여 유저 id들
        List<UUID> participantUserIds = List.of();
        if (channel.getType() == ChannelType.PRIVATE) {
            participantUserIds = userRepository.findAll().stream()
                    .map(User::getId)
                    .filter(userId -> readStatusRepository.findByUserIdAndChannelId(userId, channel.getId()).isPresent())
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
    public Channel createPublic(PublicChannelCreateRequest request) {
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
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivate(PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.ownerId() == null) {
            throw new IllegalArgumentException("ownerId must not be null");
        }
        if (request.participantUserIds() == null || request.participantUserIds().isEmpty()) {
            throw new IllegalArgumentException("participantUserIds must not be empty");
        }

        // PRIVATE 채널 name/description을 생략
        Channel channel = new Channel(ChannelType.PRIVATE, null, request.ownerId(), null);
        Channel saved = channelRepository.save(channel);

        // 참여자(요청에 포함된 유저 + owner)별 ReadStatus 생성
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

        return saved;
    }

    @Override
    public Channel update(ChannelUpdateRequest request) {
        if (request == null || request.channelId() == null) {
            throw new IllegalArgumentException("channelId must not be null");
        }

        UUID channelId = request.channelId();
        String name = request.params() == null ? null : request.params().name();
        String description = request.params() == null ? null : request.params().description();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found. id=" + channelId));

        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("channel name must not be blank");
        }

        channel.update(description, name);
        return channelRepository.save(channel);
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
                    // PRIVATE 유저가 참여한 채널만 노출
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

        /// 관련 도메인 삭제: ReadStatus
        /// ReadStatusRepository 인터페이스 범위 내에서만 삭제하기 위해,
        /// 전체 유저를 순회하며 유저별 ReadStatus를 조회 후 channelId로 필터링
        for (User user : userRepository.findAll()) {
            UUID userId = user.getId();
            for (ReadStatus rs : readStatusRepository.findAllByUserId(userId)) {
                if (channelId.equals(rs.getChannelId())) {
                    readStatusRepository.delete(rs.getId());
                }
            }
        }

        // 채널 삭제
        channelRepository.delete(channelId);
    }

    @Override
    public boolean existsById(UUID channelId) {
        return channelRepository.existsById(channelId);
    }

}