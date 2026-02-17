package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusResponse;
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
    public ReadStatusResponse markAsRead(
            UUID userId,
            UUID channelId
    ) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 User가 존재하지 않습니다."));

        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Channel이 존재하지 않습니다."));

        ReadStatus readStatus = readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .orElseGet(() -> new ReadStatus(userId, channelId));

        readStatus.markAsRead();

        readStatusRepository.save(readStatus);

        return ReadStatusResponse.from(readStatus);
    }

    @Override
    public ReadStatusResponse findByUserAndChannel(
            UUID userId,
            UUID channelId
    ) {
        return readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .map(ReadStatusResponse::from)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 User와 Channel에 대한 ReadStatus가 존재하지 않습니다."
                ));
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatusResponse::from)
                .toList();
    }

    @Override
    public void deleteByUserAndChannel(
            UUID userId,
            UUID channelId
    ) {
        readStatusRepository.deleteByUserIdAndChannelId(userId, channelId);
    }
}
