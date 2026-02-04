package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.DTO.Channel.*;
import com.sprint.mission.discodeit.status.ReadStatusInterface;
import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusInterface readStatusInterface;
    private final FileMessageRepository fileMessageRepository;

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPublic(PublicChannelCreatRequest request) {
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name(),
                request.description()

        );
        return channelRepository.save(channel);
    }

    @Override
    public Channel createPrivate(PrivateChannelCreatRequest request) {

        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );
        channelRepository.save(channel);

        for (UUID userId : request.userIds()) {
            ReadStatus readStatus = new ReadStatus(
                    UUID.randomUUID(),
                    userId,
                    channel.getId(),
                    Instant.now(),
                    Instant.now(),
                    Instant.now()
            );
            readStatusInterface.save(readStatus);
        }
        return channel;
    }

    @Override
    public ChannelFindRespone find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("없는 채널"));
        Instant lastMessageAt = fileMessageRepository
                .findLatestByChannelId(channelId)
                .map(Message::getCreatedAt)
                .orElse(null);

        List<UUID> participantUserIds = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participantUserIds = readStatusInterface
                    .findByChannel(channelId)
                    .stream()
                    .map(ReadStatus :: getUserId)
                    .toList();
        }

        return new ChannelFindRespone(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                participantUserIds
        );
    }

    public List<ChannelRespone> findByUserId(UUID userId){
        List<Channel> publicChannels = channelRepository.findAllPublic();
        List<Channel> privateChannels = channelRepository.findPrivateByUserId(userId);

        return Stream.concat(publicChannels.stream(), privateChannels.stream())
                .map(channel -> {

                    Instant lastMessageAt = fileMessageRepository
                            .findLatestByChannelId(channel.getId())
                            .map(Message::getCreatedAt)
                            .orElse(null);

                    List<UUID> participantUserIds = null;
                    if (channel.getType() == ChannelType.PRIVATE) {
                        participantUserIds = readStatusInterface
                                .findAllByChannelId(channel.getId())
                                .stream()
                                .map(ReadStatus::getUserId)
                                .toList();
                    }

                    return new ChannelRespone(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            lastMessageAt,
                            participantUserIds
                    );
                })
                .toList();
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public ChannelRespone update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("채널 없음"));
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다");
        }

        channel.update(
                request.name(),
                request.description()
        );
        Channel saved = channelRepository.save(channel);

        Instant lastMessageAt = fileMessageRepository
                .findLatestByChannelId(saved.getId())
                .map(Message::getCreatedAt)
                .orElse(null);

        return new ChannelRespone(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageAt,
                null
        );
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("없는 채널");
        }
        fileMessageRepository.deleteAllByChannel(channelId);

        readStatusInterface.deleteAllByChannelId(channelId);

        channelRepository.deleteById(channelId);
    }
}
