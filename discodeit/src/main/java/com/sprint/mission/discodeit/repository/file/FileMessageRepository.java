package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final FileLockProvider fileLockProvider;

    public FileMessageRepository(
        @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
        FileLockProvider fileLockProvider
    ) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory,
            Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.fileLockProvider = fileLockProvider;
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public void save(Message message) {
        Path path = resolvePath(message.getId());
        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public Optional<Message> findByID(UUID id) {
        Message msg = null;
        Path path = resolvePath(id);
        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                msg = (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }finally {
                lock.unlock();
            }
        }
        return Optional.ofNullable(msg);
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        ReentrantLock lock = fileLockProvider.getLock(path);
                        lock.lock();
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } finally {
                            lock.unlock();
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> findInList(List<UUID> ids){
        List<Message> results = new ArrayList<>();

        for(UUID id : ids){
          findByID(id).ifPresent(results::add);
        }
        return results;
    }
}
