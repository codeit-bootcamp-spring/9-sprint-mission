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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {

        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return readStatusRepository.findAllByUser_Id(userId).stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Override
    public ReadStatusDto findById(UUID readStatusId) {

        ReadStatus entity = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found"));

        return readStatusMapper.toDto(entity);
    }

    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {

        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        ReadStatus entity = readStatusRepository
            .findByUser_IdAndChannel_Id(userId, channelId)
            .orElseGet(() -> new ReadStatus(user, channel));

        entity.markAsRead();
        readStatusRepository.save(entity);

        return readStatusMapper.toDto(entity);
    }

    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {

        ReadStatus entity = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found"));

        entity.markAsRead();
        readStatusRepository.save(entity);

        return readStatusMapper.toDto(entity);
    }

    @Override
    public ReadStatusDto markAsRead(UUID userId, UUID channelId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("Channel not found"));

        ReadStatus entity = readStatusRepository
            .findByUser_IdAndChannel_Id(userId, channelId)
            .orElseGet(() -> new ReadStatus(user, channel));

        entity.markAsRead();
        readStatusRepository.save(entity);

        return readStatusMapper.toDto(entity);
    }

    @Override
    public ReadStatusDto markAsReadById(UUID readStatusId) {

        ReadStatus entity = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found"));

        entity.markAsRead();
        readStatusRepository.save(entity);

        return readStatusMapper.toDto(entity);
    }

    @Override
    public ReadStatusDto findByUserAndChannel(UUID userId, UUID channelId) {

        ReadStatus entity = readStatusRepository
            .findByUser_IdAndChannel_Id(userId, channelId)
            .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found"));

        return readStatusMapper.toDto(entity);
    }
}