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
import com.sprint.mission.discodeit.repository.jpa.ChannelJpaRepository;
import com.sprint.mission.discodeit.repository.jpa.ReadStatusJpaRepository;
import com.sprint.mission.discodeit.repository.jpa.UserJpaRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusJpaRepository readStatusRepository;
    private final UserJpaRepository userRepository;
    private final ChannelJpaRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new NoSuchElementException("User not found: " + request.userId()));
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new NoSuchElementException("Channel not found: " + request.channelId()));

        ReadStatus readStatus = new ReadStatus(user, channel, request.lastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException("ReadStatus not found: " + readStatusId));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = find(readStatusId);

        if (request.channelId() != null) {
            Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + request.channelId()));
        }

        if (request.lastReadAt() != null) {
            readStatus.updateLastReadAt(request.lastReadAt());
        }

        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = find(readStatusId);
        readStatusRepository.delete(readStatus);
    }

    private ReadStatus createReadStatus(User user, Channel channel, Instant lastReadAt) {
        return new ReadStatus(user, channel, lastReadAt);
    }
}
