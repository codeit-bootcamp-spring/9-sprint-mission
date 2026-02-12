package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private final String filePath;
    private Map<UUID, Channel> channelMap;
    public FileChannelRepository(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        this.channelMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadData() {
        File file = new File(this.filePath);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileChannelRepository Error] 로드 실패: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath))) {
            oos.writeObject(channelMap);
        } catch (IOException e) {
            System.err.println("[FileChannelRepository Error] 저장 실패: " + e.getMessage());
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
        for (Channel channel : channelMap.values()) {
            if (channel.getName().equals(name)) {
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : channelMap.values()) {
            if (channel.getParticipantUserIds().contains(userId)) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        if (channelMap.remove(id) != null) {
            saveData();
        }
    }
}