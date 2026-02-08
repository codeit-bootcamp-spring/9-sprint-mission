package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Profile("file")
@RequiredArgsConstructor
public class FileChannelService implements ChannelService {

    private final FileChannelRepository fileChannelRepository;

    @Override
    public ChannelResponse createPublic(CreatePublicChannelRequest request) {
        validateDuplicateName(request.name());

        Channel channel = new Channel(
                request.name(),
                request.description(),
                ChannelType.PUBLIC
        );

        Channel saved = fileChannelRepository.save(channel);

        return ChannelResponse.from(
                saved,
                null,
                List.of()
        );
    }

    @Override
    public ChannelResponse createPrivate(CreatePrivateChannelRequest request) {
        Channel channel = new Channel(
                request.participantUserIds()
        );

        Channel saved = fileChannelRepository.save(channel);

        return ChannelResponse.from(
                saved,
                null,
                saved.getParticipantIds()
        );
    }

    @Override
    public ChannelResponse findById(UUID channelId) {
        Channel channel = fileChannelRepository.findById(channelId)
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
        return fileChannelRepository.findAll().stream()
                .filter(channel -> channel.isParticipant(userId))
                .map(channel -> ChannelResponse.from(
                        channel,
                        null,
                        channel.getParticipantIds()
                ))
                .toList();
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        Channel channel = fileChannelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        validateDuplicateName(request.name());
        channel.updateName(request.name());

        Channel updated = fileChannelRepository.update(channel);

        return ChannelResponse.from(
                updated,
                null,
                updated.getChannelType() == ChannelType.PRIVATE
                        ? updated.getParticipantIds()
                        : List.of()
        );
    }

    @Override
    public void delete(UUID channelId) {
        fileChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        fileChannelRepository.delete(channelId);
    }

    private void validateDuplicateName(String name) {
        if (fileChannelRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 채널명입니다.");
        }
    }
}
