package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final String fileName = "channels.dat"; // 채널 데이터 저장 파일명

    @Override
    public Channel save(Channel channel) {
        List<Channel> channels = findAll();
        channels.removeIf(c -> c.getId().equals(channel.getId()));
        channels.add(channel);

        saveAllToFile(channels);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return findAll().stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Channel> findAll() {
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        List<Channel> channels = findAll();
        if (channels.removeIf(channel -> channel.getId().equals(id))) {
            saveAllToFile(channels);
        }
    }

    private void saveAllToFile(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}