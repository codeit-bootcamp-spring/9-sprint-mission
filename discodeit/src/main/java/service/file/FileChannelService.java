package service.file;

import entity.Channel;
import exception.NotFoundException;
import service.ChannelService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileChannelService() {
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Channel.class.getSimpleName()
        );
        ensureDirectory();
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + DIRECTORY, e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    // =========================
    // Public API
    // =========================

    @Override
    public Channel create(String name, UUID ownerId) {
        Channel channel = new Channel(name, ownerId);
        write(resolvePath(channel.getId()), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return readOrThrow(channelId);
    }

    @Override
    public List<Channel> findAll() {
        ensureDirectory();

        try (var stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::readPathAsChannel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list directory: " + DIRECTORY, e);
        }
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        Channel channel = readOrThrow(channelId);
        channel.update(newName);
        write(resolvePath(channel.getId()), channel);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        deleteFileOrThrow(channelId);
    }

    @Override
    public boolean existsById(UUID channelId) {
        if (channelId == null) return false;
        return Files.exists(resolvePath(channelId));
    }

    // =========================
    // Helper Methods
    // =========================

    private Channel readOrThrow(UUID channelId) {
        Channel channel = readOrNull(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel not found id=" + channelId);
        }
        return channel;
    }

    private Channel readOrNull(UUID channelId) {
        if (channelId == null) return null;
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) return null;
        return readPathAsChannel(path);
    }

    private Channel readPathAsChannel(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (Channel) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read channel file: " + path, e);
        }
    }

    private void write(Path path, Channel channel) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(channel);

        } catch (IOException e) {
            throw new RuntimeException("Failed to write channel file: " + path, e);
        }
    }

    private void deleteFileOrThrow(UUID channelId) {
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete channel file: " + path, e);
        }
    }
}