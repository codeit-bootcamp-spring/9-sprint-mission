package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (userRepository.find(request.getUserId()) == null) {
            throw new IllegalArgumentException("User not found.");
        }
        if (channelRepository.find(request.getChannelId()) == null) {
            throw new IllegalArgumentException("Channel not found.");
        }
        if (readStatusRepository.findByUserIdAndChannelId(request.getUserId(), request.getChannelId()) != null) {
            throw new IllegalArgumentException("ReadStatus already exists.");
        }

        ReadStatus readStatus = new ReadStatus(request.getUserId(), request.getChannelId());
        readStatusRepository.save(readStatus);
        return toResponse(readStatus);
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        ReadStatus readStatus = readStatusRepository.find(id);
        if (readStatus == null) {
            return null;
        }
        return toResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        List<ReadStatusResponse> responses = new ArrayList<>();
        for (ReadStatus readStatus : readStatusRepository.findAllByUserId(userId)) {
            responses.add(toResponse(readStatus));
        }
        return responses;
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.find(request.getId());
        if (readStatus == null) {
            throw new IllegalArgumentException("ReadStatus not found.");
        }
        readStatus.updateLastReadAt(request.getLastReadAt());
        return toResponse(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.delete(id);
    }

    private ReadStatusResponse toResponse(ReadStatus readStatus) {
        return ReadStatusResponse.builder()
                .id(readStatus.getId())
                .userId(readStatus.getUserId())
                .channelId(readStatus.getChannelId())
                .lastReadAt(readStatus.getLastReadAt())
                .createdAt(readStatus.getCreatedAt())
                .updatedAt(readStatus.getUpdatedAt())
                .build();
    }
}
