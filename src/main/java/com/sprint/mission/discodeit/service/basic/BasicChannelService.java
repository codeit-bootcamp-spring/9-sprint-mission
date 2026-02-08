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
        Channel ch = new Channel(request.name(), request.description(), ChannelType.PUBLIC);
        channelRepository.save(ch);
        return ChannelResponse.from(ch, null, List.of());
    }

    @Override
    public ChannelResponse createPrivate(CreatePrivateChannelRequest request) {
        Channel ch = new Channel(request.participantUserIds());
        channelRepository.save(ch);
        for (UUID userId : ch.getParticipantIds()) {
            ReadStatus rs = new ReadStatus(userId, ch.getId());
            readStatusRepository.save(rs);
        }
        return ChannelResponse.from(ch, null, ch.getParticipantIds());
    }

    @Override
    public ChannelResponse findById(UUID channelId) {
        Channel ch = channelRepository.findById(channelId);
        if (ch == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }
        Instant latest = latestMessageAt(channelId);
        List<UUID> participants = ch.getChannelType() == ChannelType.PRIVATE
                ? new ArrayList<>(ch.getParticipantIds()) : List.of();
        return ChannelResponse.from(ch, latest, participants);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<ChannelResponse> out = new ArrayList<>();
        for (Channel ch : channelRepository.findAllPublic()) {
            Instant latest = latestMessageAt(ch.getId());
            out.add(ChannelResponse.from(ch, latest, List.of()));
        }
        for (Channel ch : channelRepository.findPrivateChannelsByUserId(userId)) {
            Instant latest = latestMessageAt(ch.getId());
            out.add(ChannelResponse.from(ch, latest, new ArrayList<>(ch.getParticipantIds())));
        }
        return out;
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel ch = channelRepository.findById(request.channelId());
        if (ch == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }
        if (ch.getChannelType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        if (request.name() != null && !request.name().isBlank()) {
            ch.updateName(request.name());
        }
        if (request.description() != null) {
            ch.updateDescription(request.description());
        }
        channelRepository.update(ch);
        Instant latest = latestMessageAt(ch.getId());
        return ChannelResponse.from(ch, latest, List.of());
    }

    @Override
    public void delete(UUID channelId) {
        Channel ch = channelRepository.findById(channelId);
        if (ch == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }
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
