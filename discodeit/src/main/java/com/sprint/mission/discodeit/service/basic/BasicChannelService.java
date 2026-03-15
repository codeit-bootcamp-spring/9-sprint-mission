package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelJpaRepository channelRepository;
    private final ReadStatusJpaRepository readStatusRepository;
    private final MessageJpaRepository messageRepository;
    private final UserJpaRepository userRepository;

    public PageResponse<MessageDto> getMessages(Channel channel, int page) {
        Slice<Message> slice = messageRepository.findAllByChannelOrderByCreatedAtDesc(
            channel,
            PageRequest.of(page, 50)
        );

//        List<MessageDto> dtos = slice.getContent().stream()
//            .map(MessageDto::from)
//            .collect(Collectors.toList());

        Slice<MessageDto> dtoSlice = slice.map(message -> MessageMapper.toDto(message));

        return PageResponseMapper.fromSlice(dtoSlice);

    }
    @Transactional
    public Channel create(PublicChannelCreateRequest request) {
        Channel channel = Channel.createPublic(request.name(), request.description());
        return channelRepository.save(channel);
    }


    @Transactional
    public Channel create(PrivateChannelCreateRequest request) {
        Channel channel = Channel.createPrivate(request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);


        return savedChannel;
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
