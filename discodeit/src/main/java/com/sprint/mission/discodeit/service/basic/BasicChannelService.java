package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel newChannel = new Channel(ChannelType.PRIVATE, "temp", "temp");
        channelRepository.save(newChannel);

        List<User> participants = userRepository.findAllById(request.participantIds());

        List<ReadStatus> readStatusList = participants.stream().map(
            participant-> new ReadStatus(participant, newChannel)
        ).toList();

        readStatusRepository.saveAll(readStatusList);

        return channelMapper.toDto(newChannel);
    }

    @Transactional
    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request){
        Channel newChannel = new Channel(ChannelType.PUBLIC,
                request.name(),
                request.description()
        );
        channelRepository.save(newChannel);
        return channelMapper.toDto(newChannel);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id).orElseThrow(()
            -> new NoSuchElementException("Channel not found: " + id));
        channelRepository.delete(channel);
    }

    @Override
    public ChannelDto findByID(UUID id) {
        Channel channel = channelRepository.findById(id).orElseThrow(()
            -> new NoSuchElementException("Channel not found: " + id));

        return channelMapper.toDto(channel);
    }

    @Override
    public List<ChannelDto> findAll() {
        List<Channel> channelList = channelRepository.findAll();
        return channelList.stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findAllByUserId(userId).stream()
            .map(channelMapper::toDto)
            .toList();
    }


    @Transactional
    @Override
    public ChannelDto update(UUID id, ChannelUpdateRequest request) {
        Channel target = channelRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Channel not found: " + id));

        if (target.getType() == ChannelType.PRIVATE){
            throw new IllegalStateException("채널 정보 변경 실패 (PRIVATE 채널은 수정할 수 없습니다.) | ID: " + id);
        }

        target.update(request.name(), request.description());
        return channelMapper.toDto(target);
    }
}
