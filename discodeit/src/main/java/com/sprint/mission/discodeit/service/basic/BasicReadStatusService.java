package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID chId = request.channelId();

        List<ReadStatus> readStatusList = this.findAllbyUserId(userId);
        if(readStatusList .stream()
                .anyMatch(status -> status.getChannelId().equals(chId))){
            throw new IllegalStateException("create ReadStatus 오류 | 이미 해당 유저와 채널에 대한 Read Status가 존재함: "
                    + "channel - " + chId + " / user - " + userId);
        }

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId()
        );

        readStatusRepository.save(readStatus);

        return readStatus;
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findByID(id).orElseThrow();
    }

    @Override
    public List<ReadStatus> findAllbyUserId(UUID id) {
        return readStatusRepository.findAll().stream()
                .filter(readStatus -> {
                    return readStatus.getUserId().equals(id);
                })
                .toList();
    }

    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus target = readStatusRepository.findByID(id).orElseThrow();

        target.updateLastReadAt(request.newLastReadAt());
        return target;
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.remove(id);
        System.out.println("UserStatus 삭제 - ID: " + id);
    }
}
