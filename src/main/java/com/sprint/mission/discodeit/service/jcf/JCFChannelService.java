package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Profile("jcf")
@RequiredArgsConstructor
public class JCFChannelService implements ChannelService {

    private final JCFChannelRepository channelRepository;
    private final JCFMessageRepository messageRepository;
    private final JCFReadStatusRepository readStatusRepository;

    private final JCFUserRepository userRepository;
    private final JCFUserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    private final ChannelMapper channelMapper;

    @Override
    public ChannelDto createPublic(PublicChannelCreateRequest request) {

        Channel channel = new Channel(
            request.name(),
            request.description(),
            ChannelType.PUBLIC
        );

        channelRepository.save(channel);

        return channelMapper.toDto(channel, null, List.of());
    }

    @Override
    public ChannelDto createPrivate(PrivateChannelCreateRequest request) {

        Channel channel = new Channel(request.participantIds());
        channelRepository.save(channel);

        for (UUID userId : channel.getParticipantIds()) {
            readStatusRepository.save(new ReadStatus(userId, channel.getId()));
        }

        return channelMapper.toDto(channel, null, buildParticipants(channel));
    }

    @Override
    public ChannelDto findById(UUID channelId) {

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        return channelMapper.toDto(
            channel,
            getLatestMessageTime(channelId),
            buildParticipants(channel)
        );
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {

        List<Channel> channels = new ArrayList<>();
        channels.addAll(channelRepository.findAllPublic());
        channels.addAll(channelRepository.findPrivateChannelsByUserId(userId));

        return channels.stream()
            .map(channel ->
                channelMapper.toDto(
                    channel,
                    getLatestMessageTime(channel.getId()),
                    buildParticipants(channel)
                )
            )
            .toList();
    }

    @Override
    public ChannelDto update(UUID channelId, ChannelUpdateRequest request) {

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (channel.getChannelType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        if (request.newName() != null && !request.newName().isBlank()) {
            channel.updateName(request.newName());
        }

        if (request.newDescription() != null) {
            channel.updateDescription(request.newDescription());
        }

        channelRepository.update(channel);

        return channelMapper.toDto(
            channel,
            getLatestMessageTime(channelId),
            buildParticipants(channel)
        );
    }

    @Override
    public void delete(UUID channelId) {

        channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.delete(channelId);
    }

    private List<UserDto> buildParticipants(Channel channel) {

        if (channel.getChannelType() == ChannelType.PUBLIC) {
            return List.of();
        }

        return channel.getParticipantIds().stream()
            .map(userId -> {
                User user = userRepository.findById(userId).orElseThrow();
                UserStatus status =
                    userStatusRepository.findByUserId(userId).orElse(null);
                return userMapper.toDto(user, status != null);
            })
            .toList();
    }

    private Instant getLatestMessageTime(UUID channelId) {
        return messageRepository.findLatestMessageTime(channelId).orElse(null);
    }
}