package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    // [복구됨] 메시지와 읽음 상태 레포지토리 의존성 주입을 다시 활성화했습니다.
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Transactional
    @Override
    public Channel create(PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        return channelRepository.save(channel);
    }

    @Transactional
    @Override
    public Channel create(PrivateChannelCreateRequest request) {
        // null 에러를 방지하기 위해 빈 문자열을 전달합니다.
        Channel channel = new Channel(ChannelType.PRIVATE, "", "");
        return channelRepository.save(channel);
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        // 복구된 toDto 메서드를 통해 완벽한 데이터를 반환합니다.
        return toDto(channel);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        channel.update(request.newName(), request.newDescription());
        return channel;
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        // [복구됨] 채널이 삭제될 때, 해당 채널에 달린 모든 메시지와 읽음 상태 기록도 함께 삭제합니다.
        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        // 마지막으로 채널 본체를 삭제합니다.
        channelRepository.deleteById(channelId);
    }

    // [복구됨] 임시로 가짜 데이터를 넣던 로직을 지우고, 실제 DB에서 데이터를 끌어오도록 복구했습니다.
    private ChannelDto toDto(Channel channel) {
        // 1. 해당 채널의 모든 메시지를 가져옵니다.
        List<Message> messages = messageRepository.findAllByChannelId(channel.getId());

        // 2. 메시지들 중 가장 최근에 작성된 시간을 찾고, 메시지가 하나도 없다면 채널 생성 시간을 기본값으로 사용합니다.
        Instant lastMessageAt = messages.stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(channel.getCreatedAt());

        // 3. 해당 채널의 모든 읽음 상태 기록을 가져와, 어떤 유저들이 참여하고 있는지 ID 목록을 추출합니다.
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