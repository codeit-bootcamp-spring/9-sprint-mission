package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    private final List<Message> data = new ArrayList<>();

    @Override
    public void create(Message message) {
        data.add(message);
    }

    @Override
    public Message findById(UUID id) {
        for (Message m : data) {
            if (m.getId().equals(id)) return m;
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message m : data) {
            if (m.getChannelId().equals(channelId)) result.add(m);
        }
        return result;
    }

    @Override
    public boolean update(UUID id, String content) {
        Message found = findById(id);
        if (found == null) return false;

        found.update(content);
        return true;
    }

    @Override
    public boolean delete(UUID id) {
        Message found = findById(id);
        if (found == null) return false;
        return data.remove(found);
    }
}

