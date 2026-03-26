package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.type.ChannelType;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Private 채널 생성 시도: participantCount={}", request.participantIds().size());
        Channel newChannel = new Channel(ChannelType.PRIVATE, "temp", "temp");
        channelRepository.save(newChannel);

        List<User> participants = userRepository.findAllById(request.participantIds());
        log.debug("Private 채널 참가자 조회 완료: foundCount={}, requestedCount={}",
            participants.size(), request.participantIds().size());
        List<ReadStatus> readStatusList = participants.stream().map(
            participant-> new ReadStatus(participant, newChannel)
        ).toList();

        readStatusRepository.saveAll(readStatusList);
        log.info("Private 채널 생성 완료: channelId={}", newChannel.getId());
        return channelMapper.toDto(newChannel);
    }

    @Transactional
    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request){
        log.info("Public 채널 생성 시도: name={}", request.name());
        Channel newChannel = new Channel(ChannelType.PUBLIC,
                request.name(),
                request.description()
        );
        channelRepository.save(newChannel);
        log.info("Public 채널 생성 완료: id={}", newChannel.getId());
        return channelMapper.toDto(newChannel);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        log.info("채널 삭제 시도: channelId={}", id);
        Channel channel = channelRepository.findById(id).orElseThrow(()
            -> {
            log.warn("채널 삭제 실패: 존재하지 않는 채널 ID={}", id);
            return new ChannelNotFoundException(id);
        });
        log.info("채널 삭제 완료: channelId={}", id);
        channelRepository.delete(channel);
    }

    @Override
    public ChannelDto findByID(UUID id) {
        Channel channel = channelRepository.findById(id).orElseThrow(()
            -> new ChannelNotFoundException(id));

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
        log.info("채널 정보 수정 시도: id={}", id);
        Channel target = channelRepository.findById(id).orElseThrow(
            () -> {
                log.warn("채널 수정 실패: 존재하지 않는 채널 ID={}", id);
                return new ChannelNotFoundException(id);
            });

        if (target.getType() == ChannelType.PRIVATE){
            log.warn("채널 수정 실패: PRIVATE 채널은 수정할 수 없음 ID={}", id);
            throw new PrivateChannelUpdateException(id);
        }

        target.update(request.name(), request.description());
        log.info("채널 정보 수정 완료: id={}", id);
        return channelMapper.toDto(target);
    }
}
