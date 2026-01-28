package repository.file;

import entity.Message;
import repository.AbstractFileRepository;
import repository.MessageRepository;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageRepository extends AbstractFileRepository<Message> implements MessageRepository {

    private final Path directory;

    public FileMessageRepository() {
        this.directory = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                Message.class.getSimpleName()
        );
        ensureDirectory();
    }

    @Override
    protected Path directory() {
        return directory;
    }

    @Override
    public Message save(Message message) {
        if (message == null) throw new IllegalArgumentException("message is null");
        write(resolvePath(message.getId()), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        if (messageId == null) return Optional.empty();
        Path path = resolvePath(messageId);
        if (!exists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<Message> findAll() {
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
    public List<Message> findAllByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public void deleteById(UUID messageId) {
        if (messageId == null) return;
        delete(resolvePath(messageId));
    }

    @Override
    public boolean existsById(UUID messageId) {
        if (messageId == null) return false;
        return exists(resolvePath(messageId));
    }
}