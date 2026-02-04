package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final String FILE_PATH = "channels.ser";

    private FileChannelRepository() {}
    private static class Holder {
        private static final FileChannelRepository INSTANCE = new FileChannelRepository();
    }
    public static FileChannelRepository getInstance() {
        return Holder.INSTANCE;
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

    private void saveData(Map<UUID, Channel> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Channel channel) {
        Map<UUID, Channel> data = loadData();
        data.put(channel.getId(), channel);
        saveData(data);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return loadData().values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> data = loadData();
        data.remove(id);
        saveData(data);
    }
}