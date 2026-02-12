package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusDto.CreateRequest request) {
        if (userRepository.findById(request.userId()).isEmpty() ||
                channelRepository.findById(request.channelId()).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 유저 또는 채널입니다.");
        }

        if (readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 읽음 상태입니다.");
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public void update(UUID id) {
        readStatusRepository.findById(id).ifPresent(rs -> {
            rs.recordUpdate();
            readStatusRepository.save(rs);
        });
    }
}