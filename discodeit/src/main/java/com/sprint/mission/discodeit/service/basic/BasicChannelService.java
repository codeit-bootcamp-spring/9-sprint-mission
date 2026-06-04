package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.type.ChannelType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final CacheManager cacheManager;
    private final JwtRegistry jwtRegistry;

    @PersistenceContext
    private final EntityManager em;

    @Transactional
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        log.info("Private 채널 생성 시도: participantCount={}", request.participantIds().size());

        Channel newChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, "temp", "temp"));

        List<User> participants = userRepository.findAllById(request.participantIds());
        log.debug("Private 채널 참가자 조회 완료: foundCount={}, requestedCount={}",
            participants.size(), request.participantIds().size());

        List<ReadStatus> readStatusList = participants.stream().map(
            participant -> new ReadStatus(participant, newChannel)
        ).toList();

        readStatusRepository.saveAll(readStatusList);

        channelRepository.flush();
        readStatusRepository.flush();

        em.clear();

        Channel cleanChannel = channelRepository.findById(newChannel.getId())
            .orElseThrow(() -> new NoSuchElementException("채널이 존재하지 않습니다."));

        Cache cache = cacheManager.getCache("userChannels");
        if (cache != null) {
            request.participantIds().forEach(cache::evict);
        }

        log.info("Private 채널 생성 완료: channelId={}", cleanChannel.getId());

        Set<UUID> onlineUserIds = new HashSet<>(jwtRegistry.getActiveUserIds());

        return channelMapper.toDto(cleanChannel, onlineUserIds);
    }

    @Transactional
    @Override
    @CacheEvict(cacheNames = "userChannels", allEntries = true)
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request){
        log.info("Public 채널 생성 시도: name={}", request.name());
        Channel newChannel = channelRepository.save(new Channel(ChannelType.PUBLIC,
            request.name(),
            request.description()
        ));
        log.info("Public 채널 생성 완료: id={}", newChannel.getId());
        return channelMapper.toDto(newChannel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    @CacheEvict(cacheNames = "userChannels", allEntries = true)
    public void delete(UUID id) {
        log.info("채널 삭제 시도: channelId={}", id);
        Channel channel = channelRepository.findById(id).orElseThrow(()
            -> {
            log.warn("채널 삭제 실패: 존재하지 않는 채널 ID={}", id);
            return new ChannelNotFoundException(id);
        });
        channelRepository.delete(channel);
        log.info("채널 삭제 완료: channelId={}", id);
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
    @Cacheable(cacheNames = "userChannels", key = "#userId")
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> cleanChannels = channelRepository.findAllWithParticipantsByUserId(userId);

        Set<UUID> onlineUserIds = new HashSet<>(jwtRegistry.getActiveUserIds());

        return cleanChannels.stream()
            .map(channel -> channelMapper.toDto(channel, onlineUserIds))
            .toList();
    }


    @Transactional
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(cacheNames = "userChannels", allEntries = true)
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
