package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "data/channels.txt";

    public FileChannelRepository() {
        try {
            Path path = Path.of(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 생성 실패", e);
        }
    }

    private List<Channel> loadAll() {
        List<Channel> channels = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split("\\|");

                Channel channel = new Channel(
                        parts[3],                 // channelName
                        parts[4],                 // description
                        Boolean.parseBoolean(parts[5]) // isPrivate
                );

                channels.add(channel);
            }
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 읽기 실패", e);
        }

        return channels;
    }

    private void saveAll(List<Channel> channels) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Channel channel : channels) {
                writer.write(toLine(channel));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 저장 실패", e);
        }
    }

    private String toLine(Channel channel) {
        return String.join("|",
                channel.getId().toString(),
                String.valueOf(channel.getCreatedAt()),
                String.valueOf(channel.getUpdatedAt()),
                channel.getChannelName(),
                channel.getChannelDescription(),
                String.valueOf(channel.isPrivate())
        );
    }

    @Override
    public void create(Channel channel) {
        List<Channel> channels = loadAll();
        channels.add(channel);
        saveAll(channels);
    }

    @Override
    public Channel findById(UUID id) {
        for (Channel channel : loadAll()) {
            if (channel.getId().equals(id)) return channel;
        }
        return null;
    }

    @Override
    public Channel findByName(String channelName) {
        for (Channel channel : loadAll()) {
            if (channel.getChannelName().equals(channelName)) return channel;
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return loadAll();
    }

    @Override
    public boolean update(UUID id, String channelName, String channelDescription, boolean isPrivate) {
        List<Channel> channels = loadAll();

        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                channel.update(channelName, channelDescription, isPrivate);
                saveAll(channels);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        List<Channel> channels = loadAll();
        boolean removed = channels.removeIf(c -> c.getId().equals(id));
        if (removed) saveAll(channels);
        return removed;
    }
}
