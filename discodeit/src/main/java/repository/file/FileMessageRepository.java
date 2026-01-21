package repository.file;

import entity.Message;
import repository.MessageRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepository {

    private final Path directory;
    private static final String EXT = ".ser";

    public FileMessageRepository() {
        this.directory = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Message.class.getSimpleName()
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
    public Message save(Message message) {
        ensureDirectory();
        write(resolvePath(message.getId()), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        if (messageId == null) return Optional.empty();
        Path path = resolvePath(messageId);
        if (Files.notExists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<Message> findAll() {
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
    public List<Message> findAllByChannelId(UUID channelId) {
        if (channelId == null) return List.of();
        // 인덱스 없으니 전체 스캔 후 필터
        return findAll().stream()
                .filter(m -> channelId.equals(m.getChannelId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID messageId) {
        if (messageId == null) return;
        Path path = resolvePath(messageId);
        if (Files.notExists(path)) return;

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + path, e);
        }
    }

    @Override
    public boolean existsById(UUID messageId) {
        if (messageId == null) return false;
        return Files.exists(resolvePath(messageId));
    }

    private Message read(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read: " + path, e);
        }
    }

    private void write(Path path, Message message) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write: " + path, e);
        }
    }
}