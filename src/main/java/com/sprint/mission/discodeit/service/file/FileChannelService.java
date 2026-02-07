package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelResponse createPublic(ChannelCreateRequest request) {
        Channel channel = Channel.publicChannel(request.getName(), request.getDescription());
        channelRepository.save(channel);
        return toResponse(channel, null);
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        if (request.getParticipantUserIds() == null || request.getParticipantUserIds().isEmpty()) {
            throw new IllegalArgumentException("Private channel participants are required.");
        }
        for (UUID userId : request.getParticipantUserIds()) {
            if (userRepository.find(userId) == null) {
                throw new IllegalArgumentException("User not found: " + userId);
            }
        }

        Channel channel = Channel.privateChannel(request.getParticipantUserIds());
        channelRepository.save(channel);

        for (UUID userId : request.getParticipantUserIds()) {
            readStatusRepository.save(new ReadStatus(userId, channel.getId()));
        }

        return toResponse(channel, null);
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        Channel channel = channelRepository.find(channelId);
        if (channel == null) {
            return null;
        }
        Instant lastMessageAt = resolveLastMessageAt(channelId);
        return toResponse(channel, lastMessageAt);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<ChannelResponse> responses = new ArrayList<>();
        for (Channel channel : channelRepository.findAll()) {
            if (channel.getType() == ChannelType.PUBLIC || channel.getParticipantUserIds().contains(userId)) {
                Instant lastMessageAt = resolveLastMessageAt(channel.getId());
                responses.add(toResponse(channel, lastMessageAt));
            }
        }
        return responses;
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.find(request.getId());
        if (channel == null) {
            throw new IllegalArgumentException("Channel not found.");
        }
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("Private channels cannot be updated.");
        }
        channel.update(request.getName(), request.getDescription());
        return toResponse(channel, resolveLastMessageAt(channel.getId()));
    }

    @Override
    public void delete(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        for (Message message : messages) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.delete(attachmentId);
            }
            messageRepository.delete(message.getId());
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAll();
        for (ReadStatus readStatus : readStatuses) {
            if (readStatus.getChannelId().equals(channelId)) {
                readStatusRepository.delete(readStatus.getId());
            }
        }

        channelRepository.delete(channelId);
    }

    private Instant resolveLastMessageAt(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        Instant last = null;
        for (Message message : messages) {
            if (last == null || message.getCreatedAt().isAfter(last)) {
                last = message.getCreatedAt();
            }
        }
        return last;
    }

    private ChannelResponse toResponse(Channel channel, Instant lastMessageAt) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .type(channel.getType())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .lastMessageAt(lastMessageAt)
                .participantUserIds(channel.getType() == ChannelType.PRIVATE
                        ? new ArrayList<>(channel.getParticipantUserIds())
                        : null)
                .build();
    }
}
