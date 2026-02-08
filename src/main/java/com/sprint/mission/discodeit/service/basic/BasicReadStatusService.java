package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
    public ReadStatusResponse create(ReadStatusCreateRequest request) {

        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 Channel이 존재하지 않습니다."));

        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 User가 존재하지 않습니다."));

        if (readStatusRepository
                .findByUserIdAndChannelId(request.userId(), request.channelId())
                .isPresent()) {
            throw new IllegalArgumentException("이미 해당 User와 Channel에 대한 ReadStatus가 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId()
        );

        readStatusRepository.save(readStatus);

        return ReadStatusResponse.from(readStatus);
    }


    @Override
    public ReadStatusResponse findById(UUID id) {
        return readStatusRepository.findById(id)
                .map(ReadStatusResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusResponse::from)
                .toList();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus rs = readStatusRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus를 찾을 수 없습니다: " + request.id()));
        rs.updateLastReadAt();
        return ReadStatusResponse.from(readStatusRepository.update(rs));
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.delete(id);
    }
}
