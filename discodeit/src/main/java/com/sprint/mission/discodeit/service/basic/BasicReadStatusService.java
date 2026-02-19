package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusView;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public ReadStatusView create(ReadStatusCreateRequest request) {
        if (request == null || request.target() == null) {
            throw new IllegalArgumentException("request.target must not be null");
        }

        UUID channelId = request.target().channelId();
        UUID userId = request.target().userId();

        if (channelId == null) {
            throw new IllegalArgumentException("channelId must not be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel not found. id=" + channelId);
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found. id=" + userId);
        }

        if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
            throw new BusinessException(
                    "ReadStatus already exists. channelId=" + channelId + ", userId=" + userId
            );
        }

        ReadStatus readStatus = new ReadStatus(UUID.randomUUID(), userId, channelId, Instant.now());
        ReadStatus saved = readStatusRepository.save(readStatus);
        return toView(saved);
    }

    @Override
    public ReadStatusView findById(UUID readStatusId) {
        if (readStatusId == null) {
            throw new IllegalArgumentException("readStatusId must not be null");
        }

        ReadStatus found = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NotFoundException("ReadStatus not found. id=" + readStatusId));
        return toView(found);
    }

    @Override
    public List<ReadStatusView> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found. id=" + userId);
        }

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public ReadStatusView update(ReadStatusUpdateRequest request) {
        if (request == null || request.readStatusId() == null || request.params() == null) {
            throw new IllegalArgumentException("request.readStatusId and request.params are required");
        }
        if (request.params().lastReadAt() == null) {
            throw new IllegalArgumentException("lastReadAt must not be null");
        }

        ReadStatus existing = readStatusRepository.findById(request.readStatusId())
                .orElseThrow(() -> new NotFoundException("ReadStatus not found. id=" + request.readStatusId()));

        existing.markRead(request.params().lastReadAt());
        ReadStatus saved = readStatusRepository.save(existing);
        return toView(saved);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (readStatusId == null) {
            throw new IllegalArgumentException("readStatusId must not be null");
        }

        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NotFoundException("ReadStatus not found. id=" + readStatusId);
        }

        readStatusRepository.delete(readStatusId);
    }

    @Override
    public boolean existsById(UUID readStatusId) {
        if (readStatusId == null) return false;
        return readStatusRepository.existsById(readStatusId);
    }
    private ReadStatusView toView(ReadStatus readStatus) {
        return new ReadStatusView(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }
}