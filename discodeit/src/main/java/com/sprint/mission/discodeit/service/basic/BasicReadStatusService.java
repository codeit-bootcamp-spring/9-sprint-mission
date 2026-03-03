package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * ReadStatusService 인터페이스의 기본 구현체.
 * 사용자의 채널별 읽기 상태 관리 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    /**
     * 읽기 상태 정보를 저장하고 조회하는 리포지토리
     */
    private final ReadStatusRepository readStatusRepository;

    /**
     * 사용자 존재 여부 확인을 위한 리포지토리
     */
    private final UserRepository userRepository;

    /**
     * 채널 존재 여부 확인을 위한 리포지토리
     */
    private final ChannelRepository channelRepository;

    /**
     * 읽기 상태를 생성합니다.
     * 사용자와 채널의 존재 여부를 확인하고, 중복 생성을 방지합니다.
     *
     * @param request 읽기 상태 생성 요청 정보
     * @return 생성된 읽기 상태
     * @throws NoSuchElementException 사용자 또는 채널을 찾을 수 없을 경우
     * @throws IllegalArgumentException 해당 사용자와 채널의 읽기 상태가 이미 존재할 경우
     */
    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        // User 존재 여부 확인
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User not found: " + request.userId());
        }
        // Channel 존재 여부 확인
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found: " + request.channelId());
        }
        // 같은 User와 Channel의 ReadStatus가 이미 존재하는지 확인
        if (readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId()).isPresent()) {
            throw new IllegalArgumentException("ReadStatus already exists for user and channel");
        }

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt()
        );
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found: " + id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found: " + id));
        readStatus.update(request.newLastReadAt());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }
}