package repository.file;

import entity.Channel;
import repository.ChannelRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        ensureDirectory();
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path pathOf(UUID id) {
        Objects.requireNonNull(id, "id is null");
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    private void write(Channel channel) {
        Path path = pathOf(channel.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Channel read(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(channel.getId(), "channel.id is null");
        write(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        if (id == null) return Optional.empty();
        Path path = pathOf(id);
        if (Files.notExists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<Channel> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(this::read)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;
        return Files.exists(pathOf(id));
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;
        try {
            Files.deleteIfExists(pathOf(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
