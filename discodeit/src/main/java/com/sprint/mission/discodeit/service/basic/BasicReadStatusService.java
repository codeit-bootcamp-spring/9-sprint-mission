package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        // 1. 유저 존재 확인
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("유저를 찾을 수 없습니다: " + request.userId());
        }
        // 2. 채널 존재 확인
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("채널을 찾을 수 없습니다: " + request.channelId());
        }

        // 3. 중복 확인 (같은 유저가 같은 채널에 이미 ReadStatus를 가지고 있는지)
        boolean exists = readStatusRepository.findAll().stream()
                .anyMatch(rs -> rs.getUserId().equals(request.userId()) &&
                        rs.getChannelId().equals(request.channelId()));
        if (exists) {
            throw new IllegalArgumentException("이미 해당 채널의 읽음 상태가 존재합니다.");
        }

        // 4. 저장
        ReadStatus readStatus = new ReadStatus(
                UUID.randomUUID(),
                request.userId(),
                request.channelId(),
                Instant.now(),
                Instant.now(),
                Instant.now()
        );
        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public ReadStatusResponse find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId);
        if (readStatus == null) {
            throw new NoSuchElementException("읽음 상태 정보를 찾을 수 없습니다: " + readStatusId);
        }
        return toResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        // 유저 ID로 필터링
        return readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.id());
        if (readStatus == null) {
            throw new NoSuchElementException("읽음 상태 정보를 찾을 수 없습니다.");
        }

        // 시간 업데이트
        readStatus.update(request.readAt());
        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (readStatusRepository.findById(readStatusId) == null) {
            throw new NoSuchElementException("읽음 상태 정보를 찾을 수 없습니다.");
        }
        readStatusRepository.deleteById(readStatusId);
    }

    // 변환기
    private ReadStatusResponse toResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getReadAt()
        );
    }
}