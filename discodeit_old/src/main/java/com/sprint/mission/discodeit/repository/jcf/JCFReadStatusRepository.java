package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private final List<ReadStatus> data = new ArrayList<>();

    @Override
    public void create(ReadStatus readStatus) {
        data.add(readStatus);
    }

    @Override
    public ReadStatus findById(UUID id) {
        for (ReadStatus rs : data) {
            if (rs.getId().equals(id)) return rs;
        }
        return null;
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : data) {
            if (rs.getUserId().equals(userId)) result.add(rs);
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : data) {
            if (rs.getChannelId().equals(channelId)) result.add(rs);
        }
        return result;
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus rs : data) {
            if (rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId)) return rs;
        }
        return null;
    }

    @Override
    public boolean delete(UUID id) {
        return data.removeIf(rs -> rs.getId().equals(id));
    }

    @Override
    public int deleteAllByChannelId(UUID channelId) {
        int before = data.size();
        data.removeIf(rs -> rs.getChannelId().equals(channelId));
        return before - data.size();
    }
}


