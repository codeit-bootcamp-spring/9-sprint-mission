package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Profile("jcf")
@RequiredArgsConstructor
public class JCFChannelService implements ChannelService {

    private final JCFChannelRepository jcfChannelRepository;

    @Override
    public ChannelResponse createPublic(PublicChannelCreateRequest request) {
        validateDuplicateName(request.name());

        Channel channel = new Channel(
                request.name(),
                request.description(),
                ChannelType.PUBLIC
        );

        Channel saved = jcfChannelRepository.save(channel);

        return ChannelResponse.from(saved, null, List.of());
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(request.participantIds());
        Channel saved = jcfChannelRepository.save(channel);

        return ChannelResponse.from(
                saved,
                null,
                saved.getParticipantIds()
        );
    }

    @Override
    public ChannelResponse findById(UUID channelId) {
        Channel channel = jcfChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        return ChannelResponse.from(
                channel,
                null,
                channel.getChannelType() == ChannelType.PRIVATE
                        ? channel.getParticipantIds()
                        : List.of()
        );
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return jcfChannelRepository.findAll().stream()
                .filter(channel ->
                        channel.getChannelType() == ChannelType.PUBLIC
                                || channel.isParticipant(userId)
                )
                .map(channel -> ChannelResponse.from(
                        channel,
                        null,
                        channel.getChannelType() == ChannelType.PRIVATE
                                ? channel.getParticipantIds()
                                : List.of()
                ))
                .toList();
    }

    @Override
    public ChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
        Channel channel = jcfChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        if (channel.getChannelType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        validateDuplicateName(request.newName());
        channel.updateName(request.newName());

        Channel updated = jcfChannelRepository.update(channel);

        return ChannelResponse.from(updated, null, List.of());
    }

    @Override
    public void delete(UUID channelId) {
        jcfChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        jcfChannelRepository.delete(channelId);
    }

    private void validateDuplicateName(String name) {
        if (jcfChannelRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 채널명입니다.");
        }
    }
}
