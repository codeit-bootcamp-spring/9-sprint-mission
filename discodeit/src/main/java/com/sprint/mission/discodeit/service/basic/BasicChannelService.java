package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;


    @Override
    public ChannelResponseDto createPublic(ChannelCreateRequestDto requestDto) {
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                requestDto.name(),
                requestDto.description()
        );

        channelRepository.save(channel);

        return toResponse(channel);
    }

    @Override
    public ChannelResponseDto createPrivate(ChannelCreateRequestDto requestDto) {
        Channel channel = new Channel(
                ChannelType.PRIVATE,
                requestDto.name(),
                requestDto.description()
        );

        channelRepository.save(channel);

        return toResponse(channel);
    }


    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("Channel with id " + channelId + " not found")
                );

        return toResponse(channel);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        // 현재 단계에서는 유저-채널 매핑이 없으므로 전체 조회
        // (추후 ChannelUser / UserChannel 테이블 생기면 여기서 필터)
        return channelRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public ChannelResponseDto update(UUID channelId, ChannelUpdateRequestDto requestDto) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("Channel with id " + channelId + " not found")
                );

        // PRIVATE 채널은 수정 불가
        if (channel.isPrivate()) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(
                requestDto.name(),
                requestDto.description()
        );

        channelRepository.save(channel);

        return toResponse(channel);
    }


    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        channelRepository.deleteById(channelId);
    }


    private ChannelResponseDto toResponse(Channel channel) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                null,
                List.of()
        );
    }
}
