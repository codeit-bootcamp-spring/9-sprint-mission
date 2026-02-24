package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
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

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("해당 유저를 찾을 수 없습니다. ID: " + request.userId());
        }
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("해당 채널을 찾을 수 없습니다. ID: " + request.channelId());
        }
        boolean isDuplicate = readStatusRepository.findAllByUserId(request.userId()).stream()
                .anyMatch(rs -> rs.getChannelId().equals(request.channelId()));

        if (isDuplicate) {
            throw new IllegalStateException("이미 이 채널에 대한 유저의 읽음 상태가 존재합니다.");
        }
        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadMessageId()
        );
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));
    }
//매개변수로 id를 받아서 readStatusRepository.findById메서드를 호출하고 리턴값으로 반환된 데이터(ReadStatus)가
//있으면 그 데이터를 반환하고 없을시 오류를 생성한다

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }
//매개변수로 받은 userId를 사용해 모든 ReadStatus 데이터를 리스트 형태로 조회하여 반환한다

    @Override
    public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = find(id);
        readStatus.update(request.lastReadMessageId());
        return readStatusRepository.save(readStatus);
    }
/* 매개변수로 id와 ReadStatusUpdateRequest를 받아온다 매개변수로 받아온 id를 find 메서드에 넣어서
데이터를 찾고 변수에 담는다 request에 담긴 마지막으로 읽은 메세지id값을 가져와서 readStatus 정보를 업데이트한다
 readStatusRepository.save 메서드의 리턴값을 반환한다
 */
    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException("ReadStatus not found");
        }
        readStatusRepository.deleteById(id);
    }
}
/* id를 매개변수로 받아서 readStatusRepository.existsById메서드의 매개변수에 id를 넣어주고 리턴값이
false일때 오류를 반환한다 아닐시 readStatusRepository.deleteById메서드의 매개변수에 id를 넣어서 호출한다
 */