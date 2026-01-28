package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.util.*;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private final String FILE_PATH = "channels.ser";
    private Map<UUID, Channel> channelMap;

    public FileChannelRepository() {
        this.channelMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channelMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        saveData();
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return channelMap.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void delete(UUID id) {
        if (channelMap.remove(id) != null) {
            saveData();
        }
    }
}