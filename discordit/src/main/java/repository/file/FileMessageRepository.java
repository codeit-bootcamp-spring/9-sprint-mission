package repository.file;

import entity.Message;
import repository.MessageRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileMessageRepository implements MessageRepository {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileMessageRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
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

    private void write(Message message) {
        Path path = pathOf(message.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Message read(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message save(Message message) {
        Objects.requireNonNull(message, "message is null");
        Objects.requireNonNull(message.getId(), "message.id is null");
        write(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (id == null) return Optional.empty();
        Path path = pathOf(id);
        if (Files.notExists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<Message> findAll() {
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
