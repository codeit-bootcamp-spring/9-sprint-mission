package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;

    public JCFChannelService(ChannelRepository channelRepository,
                             MessageRepository messageRepository,
                             UserRepository userRepository,
                             ReadStatusRepository readStatusRepository) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.readStatusRepository = readStatusRepository;
    }

    // ===== 기존 CRUD (유지) =====
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
        return channelRepository.delete(id);
    }

    // ===== DTO 기반 (추가 요구사항) =====
    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        Channel channel = new Channel(
                request.name(),
                request.description(),
                request.isPrivate()
        );

        channelRepository.create(channel);

        // JCF 서비스에서는 PRIVATE 참가자/ReadStatus 생성까지 강제 안하면 최소 구현으로 둬도 됨
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
        List<ChannelResponse> result = new ArrayList<>();
        for (Channel ch : channelRepository.findAll()) {
            result.add(toResponse(ch));
        }
        return result;
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        // JCF에서는 ReadStatusRepository 메서드가 정확히 뭔지에 따라 달라서
        // 최소 구현: PUBLIC 채널만 반환 (혹은 전체 반환)
        //  repo 메서드 확정되면 여기 필터만 바꾸면 됨.
        return findAllDto();
    }

    private ChannelResponse toResponse(Channel channel) {
        List<Message> messages = messageRepository.findByChannelId(channel.getId());
        Instant lastMessageAt = (messages == null || messages.isEmpty())
                ? null
                : messages.get(messages.size() - 1).getCreatedAt();

        // PRIVATE 참가자 목록은 ReadStatusRepository 시그니처 확정 전이면 null로 둬도 됨
        List<UUID> participants = channel.isPrivate() ? new ArrayList<>() : null;

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


