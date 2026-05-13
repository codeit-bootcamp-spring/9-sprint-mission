package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class FileUserStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final FileLockProvider fileLockProvider;

    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
            FileLockProvider fileLockProvider
    ) {
        this.fileLockProvider = fileLockProvider;

        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, UserStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    public UserStatus save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());

        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(userStatus);
            return userStatus;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Optional<UserStatus> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            return Optional.empty();
        }

        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            UserStatus userStatus = (UserStatus) ois.readObject();
            return Optional.of(userStatus);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(userStatus -> userStatus.getUser().getId().equals(userId))
                .findFirst();
    }

    public List<UserStatus> findAll() {
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
                            return (UserStatus) ois.readObject();
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

    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    public void deleteById(UUID id) {
        Path path = resolvePath(id);

        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void deleteByUserId(UUID userId) {
        // findAll() 내부에서 각 파일별 lock을 걸고 읽고 있으니,
        // 여기서는 조회 후 해당 파일 deleteById()에서 lock을 걸면 충분
        this.findByUserId(userId)
                .ifPresent(userStatus -> this.deleteById(userStatus.getId()));
    }
}
