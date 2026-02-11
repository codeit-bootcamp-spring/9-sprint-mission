package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;

    @Override
    public void create(UUID userId, UUID channelId){
        readStatusRepository.create(userId, channelId);
    }

    @Override
    public Instant find(UUID id) {
        return readStatusRepository.find(id);
    }
    @Override
    public void findAllByUserId(UUID userId) {
        Map<UUID, Instant> result = readStatusRepository.findAllByUserId(userId);
    }
    @Override
    public boolean update(UUID userId, String channelName) {
        UUID channelId =  channelRepository.channelNameToId(channelName);
        try {
            readStatusRepository.update(userId, channelId);
            return true;
        } catch (Exception e) {
            return false;
        }
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
