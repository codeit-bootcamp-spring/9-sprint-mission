package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.AbstractFileRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelRepository extends AbstractFileRepository<Channel> implements ChannelRepository {

    private final Path directory;

    public FileChannelRepository() {
        this.directory = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Channel.class.getSimpleName()
        );
        ensureDirectory();
    }

    @Override
    protected Path directory() {
        return directory;
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null) throw new IllegalArgumentException("channel is null");
        write(resolvePath(channel.getId()), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        if (channelId == null) return Optional.empty();
        Path path = resolvePath(channelId);
        if (!exists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<Channel> findAll() {
        ensureDirectory();
        try (var stream = Files.list(directory)) {
            return stream
                    .filter(p -> p.getFileName().toString().endsWith(".ser"))
                    .map(this::read)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to list directory: " + directory, e);
        }
    }

    @Override
    public void deleteById(UUID channelId) {
        if (channelId == null) return;
        delete(resolvePath(channelId));
    }

    @Override
    public boolean existsById(UUID channelId) {
        if (channelId == null) return false;
        return exists(resolvePath(channelId));
    }
}
