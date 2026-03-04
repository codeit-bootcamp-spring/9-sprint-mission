package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.NoSuchElementException;
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
    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        if (userRepository.findById(userId).isEmpty()){
            throw new NoSuchElementException("create ReadStatus 오류 | 유저가 존재하지 않음: " + userId);
        }

        if (channelRepository.findByID(channelId).isEmpty()){
            throw new NoSuchElementException("create ReadStatus 오류 | 채널이 존재하지 않음: " + channelId);
        }

        List<ReadStatus> readStatusList = this.findAllbyUserId(userId);
        if(readStatusList .stream()
                .anyMatch(status -> status.getChannelId().equals(channelId))){
            throw new IllegalStateException("create ReadStatus 오류 | 이미 해당 유저와 채널에 대한 Read Status가 존재함: "
                    + "channel - " + channelId + " / user - " + userId);
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
