package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileChannelRepository {

    private final Path storageDir = Paths.get(System.getProperty("user.dir"), "file-data-map", "Channel");

    public FileChannelRepository() {
        try {
            Files.createDirectories(storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(Channel channel) {
        Path file = storageDir.resolve(channel.getId() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel findById(UUID id) {
        Path file = storageDir.resolve(id + ".ser");
        if (!Files.exists(file)) throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        try {
            Files.list(storageDir).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    channels.add((Channel) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channels;
    }

    public void delete(UUID id) {
        Path file = storageDir.resolve(id + ".ser");
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
