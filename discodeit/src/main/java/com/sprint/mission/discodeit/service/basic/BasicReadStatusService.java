package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(request.userId()).orElseThrow();
        Channel channel = channelRepository.findById(request.channelId()).orElseThrow();

        if(readStatusRepository.existsByUserId(userId)){
            throw new IllegalStateException("create ReadStatus 오류 | 이미 해당 유저와 채널에 대한 Read Status가 존재함: "
                    + "channel - " + channelId + " / user - " + userId);
        }

        ReadStatus readStatus = new ReadStatus(
            user,
            channel
        );

        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto find(UUID id) {
        return readStatusRepository.findById(id)
            .map(readStatusMapper::toDto)
            .orElseThrow();
    }

    @Override
    public List<ReadStatusDto> findAllbyUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
            .stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus target = readStatusRepository.findById(id).orElseThrow();

        target.updateLastReadAt(request.newLastReadAt());
        return readStatusMapper.toDto(target);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}
