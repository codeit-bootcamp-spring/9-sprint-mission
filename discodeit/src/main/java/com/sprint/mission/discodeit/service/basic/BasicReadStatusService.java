package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.DTO.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.service.DTO.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.status.ReadStatusInterface;
import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusInterface readStatusInterface;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public void deleteById(UUID id) {
        readStatusInterface.deleteById(id);
    }

    @Override
    public boolean existByUserIdAndChannelId(UUID userId, UUID channelId) {
        return false;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusInterface.findAllByUserId(userId);
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatusInterface.findBy(userId, channelId)
                .orElseThrow(() -> new NoSuchElementException("읽지 않음"));
    }

    @Override
    public ReadStatus findByUserId(UUID id) {
        return readStatusInterface.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus 없음"));
    }

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {

        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("없는 유저");
        }

        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("없는 채널");
        }

        if (readStatusInterface
                .findBy(request.userId(), request.channelId())
                .isPresent()) {
            throw new IllegalStateException("이미 읽은 상태입니다.");
        }

        ReadStatus readStatus = new ReadStatus(
                UUID.randomUUID(),
                request.userId(),
                request.channelId(),
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
        readStatusInterface.save(readStatus);
        return readStatus;
    }

    public ReadStatus update(ReadStatusUpdateRequest request){
        ReadStatus readStatus = readStatusInterface.findById(request.readStatusId())
                .orElseThrow(() -> new NoSuchElementException("이미 읽은 상태입니다"));

        readStatus.updateLastRead(request.lastRead());
        readStatusInterface.save(readStatus);

        return readStatus;
    }

    public void delete(UUID id){
        readStatusInterface.deleteById(id);
    }


}
