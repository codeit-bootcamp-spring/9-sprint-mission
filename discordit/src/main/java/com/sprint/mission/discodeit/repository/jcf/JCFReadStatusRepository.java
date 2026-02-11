package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> idReadStatusMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<ReadStatus>> userIdReadStatusMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<ReadStatus>> channelIdReadStatusMap = new ConcurrentHashMap<>();

    @Override
    public void create(UUID userId, UUID channelId) {
        ReadStatus readStatus = new ReadStatus(userId, channelId);
        idReadStatusMap.put(readStatus.getId(), readStatus);
        userIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(readStatus);
        channelIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(readStatus);
    }

    @Override
    public Instant find(UUID id) {
        return idReadStatusMap.get(id).getUpdateAt();
    }

    @Override
    public Map<UUID, Instant> findAllByUserId(UUID userId) {
        List<ReadStatus> tempList = userIdReadStatusMap.get(userId);
        Map<UUID, Instant> result = new HashMap<>();
        for(ReadStatus temp : tempList) {
            result.put(temp.getChannelId(), temp.getUpdateAt());
        }

        return result;
    }

    @Override
    public boolean update(UUID userId, UUID channelId) {
        ReadStatus temp = userIdReadStatusMap.get(userId).stream().filter(readStatus -> readStatus.getChannelId().equals(channelId)).findFirst().orElse(null);

        if(temp == null) throw new NotFound("상태값을 찾지 못했습니다.");

        temp.updateReadAt();
        return true;
    }

    @Override
    public boolean delete(UUID id) {
        ReadStatus temp = idReadStatusMap.remove(id);
        userIdReadStatusMap.remove(temp.getUserId());
        channelIdReadStatusMap.remove(temp.getChannelId());
        return true;
    }

    @Override
    public void deleteForChannel(UUID channelId) {
        List<ReadStatus> temp = channelIdReadStatusMap.get(channelId);
        temp.forEach(readStatus -> delete(readStatus.getId()));
    }

    @Override
    public void deleteForUser(UUID userId) {
        List<ReadStatus> temp = userIdReadStatusMap.get(userId);
        temp.forEach(readStatus -> delete(readStatus.getId()));
    }
}
