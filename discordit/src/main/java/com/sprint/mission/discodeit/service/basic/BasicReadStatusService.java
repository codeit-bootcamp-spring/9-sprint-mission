package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RequestCreateReadStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;

    @Override
    public List<ResponseReadStatus> create(RequestCreateReadStatusDto request){
        return readStatusRepository.create(request);
    }

    @Override
    public Instant find(UUID id) {
        return readStatusRepository.find(id);
    }
    @Override
    public List<ResponseReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }
    @Override
    public ResponseReadStatus update(UUID userId, UUID channelId) {
        return readStatusRepository.update(userId, channelId);
    }
    @Override
    public void deleteForChannel(UUID channelId) {
        readStatusRepository.deleteForChannel(channelId);
    }
    @Override
    public void deleteForUser(UUID userId) {
        readStatusRepository.deleteForUser(userId);
    }
}
