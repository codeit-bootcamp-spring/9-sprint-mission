package repository.file;

import entity.Channel;
import repository.ChannelRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelRepository implements ChannelRepository {

    private final Path directory;
    private static final String EXT = ".ser";

    public FileChannelRepository() {
        this.directory = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Channel.class.getSimpleName()
        );
        ensureDirectory();
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + directory, e);
        }
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id.toString() + EXT);
    }

    @Override
    public Channel save(Channel channel) {
        ensureDirectory();
        write(resolvePath(channel.getId()), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        if (channelId == null) return Optional.empty();
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<Channel> findAll() {
        ensureDirectory();
        try (var stream = Files.list(directory)) {
            return stream
                    .filter(p -> p.getFileName().toString().endsWith(EXT))
                    .map(this::read)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list directory: " + directory, e);
        }
    }

    @Override
    public void deleteById(UUID channelId) {
        if (channelId == null) return;
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) return;

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + path, e);
        }
    }

    @Override
    public boolean existsById(UUID channelId) {
        if (channelId == null) return false;
        return Files.exists(resolvePath(channelId));
    }

    private Channel read(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read: " + path, e);
        }
    }

    private void write(Path path, Channel channel) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write: " + path, e);
        }
    }
}
