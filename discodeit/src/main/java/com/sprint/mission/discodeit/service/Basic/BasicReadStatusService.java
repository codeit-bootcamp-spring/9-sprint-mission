package com.sprint.mission.discodeit.service.Basic;

import com.sprint.mission.discodeit.DTO.ReadStatusService.Request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.DTO.ReadStatusService.Request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserService userService;
    private final ChannelService channelService;

    @Override
    public ReadStatus create(CreateReadStatusRequest request) {
        UUID userId = request.userId();
        UUID chId = request.channelId();

        if (userService.findByID(userId) == null){
            throw new NoSuchElementException("create ReadStatus 오류 | 유저가 존재하지 않음: " + userId);
        }

        if (channelService.findByID(chId) == null){
            throw new NoSuchElementException("create ReadStatus 오류 | 채널이 존재하지 않음: " + userId);
        }

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
        return readStatusRepository.findByID(id);
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
    public ReadStatus update(UpdateReadStatusRequest request) {
        ReadStatus target = readStatusRepository.findByID(request.id());

        // 아직 세부구현을 안해서 뭘 업데이트 해야하는지 모르겠음
        return target;
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.remove(id);
    }
}
