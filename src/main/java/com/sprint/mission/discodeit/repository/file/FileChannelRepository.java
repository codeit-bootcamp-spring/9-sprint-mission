package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

@Repository
public class FileChannelRepository implements ChannelRepository {

    private final File file;
    private final Map<UUID, Channel> data;

    public FileChannelRepository() {
        Path directory = Path.of(System.getProperty("user.dir"), "file-data", "channel");
        File dir = directory.toFile();

        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("채널 디렉토리 생성 실패: " + dir.getAbsolutePath());
        }

        this.file = directory.resolve("channels.ser").toFile();
        this.data = load();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        persist();
        return channel;
    }

    @Override
    public Channel findById(UUID Id) {
        return data.get(Id);
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return data.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        data.put(channel.getId(), channel);
        persist();
        return channel;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        persist();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("채널 로딩 실패", e);
        }
    }

    private void persist() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패", e);
        }
    }
}
