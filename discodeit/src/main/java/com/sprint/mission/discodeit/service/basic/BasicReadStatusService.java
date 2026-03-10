package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    // 데이터베이스 상태를 변경하므로 트랜잭션을 적용합니다.
    @Transactional
    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        // 1. 단순 ID가 아닌 실제 User 객체를 데이터베이스에서 찾아옵니다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " does not exist"));

        // 2. 단순 ID가 아닌 실제 Channel 객체를 데이터베이스에서 찾아옵니다.
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));

        // 3. 비효율적인 스트림 검색을 버리고, 데이터베이스에 직접 중복 여부를 물어봅니다 (JPA 쿼리 메서드 활용).
        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new IllegalArgumentException("ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
        }

        // 4. UUID와 시간을 넘기는 것이 아니라, 찾아온 실제 객체를 넘겨 엔티티를 생성합니다. (시간은 내부에서 자동으로 현재 시간으로 세팅됩니다)
        ReadStatus readStatus = new ReadStatus(user, channel);

        // 5. 생성된 엔티티를 저장하고 반환합니다.
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        // 스트림 변환 없이 레포지토리에서 바로 리스트를 반환하도록 간결하게 수정했습니다.
        return readStatusRepository.findAllByUserId(userId);
    }

    // 데이터베이스 상태를 변경하므로 트랜잭션을 적용합니다.
    @Transactional
    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        // 1. 갱신할 읽음 상태 엔티티를 찾습니다.
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

        // 2. 파라미터 없는 update() 메서드를 호출하여 엔티티가 스스로 현재 시간으로 갱신하도록 합니다. (더티 체킹)
        readStatus.update();

        return readStatus;
    }

    // 데이터베이스 상태를 변경하므로 트랜잭션을 적용합니다.
    @Transactional
    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}