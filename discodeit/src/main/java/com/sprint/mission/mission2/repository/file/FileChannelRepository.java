package com.sprint.mission.mission2.repository.file;

import com.sprint.mission.mission2.entity.Channel;
import com.sprint.mission.mission2.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private static final String channelData = "channel.dat";

    private Map<UUID, Channel> load() {
        File file = new File(channelData);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("불러오기 실패", e);
        }
    }

    @Override
    public void save(Channel channel) {
        Map<UUID, Channel> channels = load();
        channels.put(channel.getId(), channel);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelData))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패", e);
        }
    }

    @Override
    public Channel read(UUID id) {
        Map<UUID, Channel> channels = load();
        return channels.get(id);
    }

    @Override
    public List<Channel> readAll() {
        Map<UUID, Channel> channels = load();
        return new ArrayList<>(channels.values());
    }

    @Override
    public void remove(UUID id) {
        Map<UUID, Channel> channels = load();
        channels.remove(id);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(channelData))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패", e);
        }
    }
}
