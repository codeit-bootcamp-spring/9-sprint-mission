package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ChannelResponse createPublic(CreatePublicChannelRequest request) {
        Channel channel = new Channel(request.name(), request.description(), ChannelType.PUBLIC);
        channelRepository.save(channel);
        return ChannelResponse.from(channel, null, List.of());
    }

    @Override
    public ChannelResponse createPrivate(CreatePrivateChannelRequest request) {
        Channel channel = new Channel(request.participantUserIds());
        channelRepository.save(channel);

        for (UUID userId : channel.getParticipantIds()) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId());
            readStatusRepository.save(readStatus);
        }

        return ChannelResponse.from(channel, null, channel.getParticipantIds());
    }

    @Override
    public ChannelResponse findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        Instant latest = latestMessageAt(channelId);
        List<UUID> participants =
                channel.getChannelType() == ChannelType.PRIVATE
                        ? new ArrayList<>(channel.getParticipantIds())
                        : List.of();

        return ChannelResponse.from(channel, latest, participants);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<ChannelResponse> out = new ArrayList<>();

        for (Channel channel : channelRepository.findAllPublic()) {
            Instant latest = latestMessageAt(channel.getId());
            out.add(ChannelResponse.from(channel, latest, List.of()));
        }

        for (Channel channel : channelRepository.findPrivateChannelsByUserId(userId)) {
            Instant latest = latestMessageAt(channel.getId());
            out.add(ChannelResponse.from(channel, latest, new ArrayList<>(channel.getParticipantIds())));
        }

        return out;
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        if (channel.getChannelType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        if (request.name() != null && !request.name().isBlank()) {
            channel.updateName(request.name());
        }
        if (request.description() != null) {
            channel.updateDescription(request.description());
        }

        channelRepository.update(channel);
        Instant latest = latestMessageAt(channel.getId());
        return ChannelResponse.from(channel, latest, List.of());
    }

    @Override
    public void delete(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.delete(channelId);
    }

    private Instant latestMessageAt(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);
    }
}
