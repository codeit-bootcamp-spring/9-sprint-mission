package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;       // 추가됨
    private final ReadStatusRepository readStatusRepository; // 추가됨

    @Override
    public ChannelResponse createPublic(PublicChannelCreateRequest request) {
        // PUBLIC 채널은 이름과 설명이 필수
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        channelRepository.save(channel);
        return toResponse(channel);
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        // PRIVATE 채널은 이름/설명 없이 생성
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        // 초대된 유저별로 ReadStatus 생성 (이게 있어야 참여자로 인정됨)
        for (UUID userId : request.participantIds()) {
            ReadStatus readStatus = new ReadStatus(
                    UUID.randomUUID(),
                    userId,
                    channel.getId(),
                    Instant.now(), // 읽은 시간
                    Instant.now(),
                    Instant.now()
            );
            readStatusRepository.save(readStatus);
        }

        return toResponse(channel);
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다: " + channelId));
        return toResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        // [조회 조건]
        // 1. PUBLIC 채널은 누구나 볼 수 있음
        // 2. PRIVATE 채널은 내가 참여(ReadStatus 존재)한 경우만 볼 수 있음

        List<Channel> allChannels = channelRepository.findAll();
        List<ChannelResponse> result = new ArrayList<>();

        for (Channel channel : allChannels) {
            if (channel.getType() == ChannelType.PUBLIC) {
                result.add(toResponse(channel));
            } else {
                // PRIVATE 채널이면 내가 참여자인지 확인
                boolean isParticipant = readStatusRepository.findAll().stream()
                        .anyMatch(rs -> rs.getChannelId().equals(channel.getId()) && rs.getUserId().equals(userId));

                if (isParticipant) {
                    result.add(toResponse(channel));
                }
            }
        }
        return result;
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

        // [검증] PRIVATE 채널은 수정 불가
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");
        }

        channel.update(request.name(), request.description());
        channelRepository.save(channel);

        return toResponse(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("채널을 찾을 수 없습니다.");
        }

        // 1. 관련된 ReadStatus(읽음 정보) 삭제
        List<ReadStatus> statuses = readStatusRepository.findAll();
        for (ReadStatus status : statuses) {
            if (status.getChannelId().equals(channelId)) {
                readStatusRepository.deleteById(status.getId());
            }
        }

        // 2. 관련된 Message(메시지) 삭제
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages) {
            if (message.getChannelId().equals(channelId)) {
                messageRepository.deleteById(message.getId());
            }
        }

        // 3. 채널 삭제
        channelRepository.deleteById(channelId);
    }

    // [변환기] Channel -> ChannelResponse DTO
    private ChannelResponse toResponse(Channel channel) {
        // 1. 가장 최근 메시지 시간 찾기
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(m -> m.getChannelId().equals(channel.getId()))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);

        // 2. PRIVATE 채널인 경우 참여자 ID 목록 찾기
        List<UUID> participantIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantIds = readStatusRepository.findAll().stream()
                    .filter(rs -> rs.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId)
                    .collect(Collectors.toList());
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                participantIds
        );
    }
}