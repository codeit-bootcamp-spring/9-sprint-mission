package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ReadStatus;
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

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatusResponse::from)
            .toList();
    }

    @Override
    public ReadStatusResponse findById(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
            .map(ReadStatusResponse::from)
            .orElseThrow(() -> new IllegalArgumentException("ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("Channel with id " + channelId + " not found"));

        ReadStatus readStatus = readStatusRepository
            .findByUserIdAndChannelId(userId, channelId)
            .orElseGet(() -> new ReadStatus(userId, channelId));

        readStatus.markAsRead();
        readStatusRepository.save(readStatus);

        return ReadStatusResponse.from(readStatus);
    }

    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new IllegalArgumentException("ReadStatus with id " + readStatusId + " not found"));

        readStatus.markAsRead();
        readStatusRepository.save(readStatus);

        return ReadStatusResponse.from(readStatus);
    }

    @Override
    public ReadStatusResponse markAsRead(UUID userId, UUID channelId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("Channel with id " + channelId + " not found"));

        ReadStatus readStatus = readStatusRepository
            .findByUserIdAndChannelId(userId, channelId)
            .orElseGet(() -> new ReadStatus(userId, channelId));

        readStatus.markAsRead();
        readStatusRepository.save(readStatus);

        return ReadStatusResponse.from(readStatus);
    }

    @Override
    public ReadStatusResponse markAsReadById(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new IllegalArgumentException("ReadStatus with id " + readStatusId + " not found"));

        readStatus.markAsRead();
        readStatusRepository.save(readStatus);

        return ReadStatusResponse.from(readStatus);
    }

    @Override
    public ReadStatusResponse findByUserAndChannel(UUID userId, UUID channelId) {
        return readStatusRepository.findByUserIdAndChannelId(userId, channelId)
            .map(ReadStatusResponse::from)
            .orElseThrow(() -> new IllegalArgumentException(
                "ReadStatus with userId " + userId + " and channelId " + channelId + " not found"
            ));
    }
}