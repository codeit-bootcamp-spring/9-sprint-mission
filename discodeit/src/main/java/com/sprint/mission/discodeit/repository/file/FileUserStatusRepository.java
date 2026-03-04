//package com.sprint.mission.discodeit.repository.file;
//
//import com.sprint.mission.discodeit.entity.UserStatus;
//import com.sprint.mission.discodeit.repository.UserStatusRepository;
//import java.util.concurrent.locks.ReentrantLock;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Repository;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.UUID;
//import java.util.stream.Stream;
//
//@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
//@Repository
//public class FileUserStatusRepository implements UserStatusRepository {
//    private final Path DIRECTORY;
//    private final String EXTENSION = ".ser";
//    private final FileLockProvider fileLockProvider;
//
//    public FileUserStatusRepository(
//        @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
//        FileLockProvider fileLockProvider
//    ) {
//        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory,
//            UserStatus.class.getSimpleName());
//        if (Files.notExists(DIRECTORY)) {
//            try {
//                Files.createDirectories(DIRECTORY);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        this.fileLockProvider = fileLockProvider;
//    }
//
//    private Path resolvePath(UUID id) {
//        return DIRECTORY.resolve(id + EXTENSION);
//    }
//
//    @Override
//    public void save(UserStatus userStatus) {
//        Path path = resolvePath(userStatus.getId());
//        ReentrantLock lock = fileLockProvider.getLock(path);
//        lock.lock();
//
//        try (
//            FileOutputStream fos = new FileOutputStream(path.toFile());
//            ObjectOutputStream oos = new ObjectOutputStream(fos)
//        ) {
//            oos.writeObject(userStatus);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    @Override
//    public boolean remove(UUID id) {
//        Path path = resolvePath(id);
//        if (Files.notExists(path)) {
//            throw new NoSuchElementException("UserStatus with id " + id + " not found");
//        }
//        try {
//            Files.delete(path);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("UserStatus 삭제 - ID: " + id);
//        return true;
//    }
//
//    @Override
//    public Optional<UserStatus> findByID(UUID id) {
//        UserStatus userStatus = null;
//        Path path = resolvePath(id);
//        ReentrantLock lock = fileLockProvider.getLock(path);
//        lock.lock();
//        if (Files.exists(path)) {
//            try (
//                FileInputStream fis = new FileInputStream(path.toFile());
//                ObjectInputStream ois = new ObjectInputStream(fis)
//            ) {
//                userStatus = (UserStatus) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            } finally {
//                lock.unlock();
//            }
//        }
//        return Optional.ofNullable(userStatus);
//    }
//
//    @Override
//    public Optional<UserStatus> findByUserId(UUID userId) {
//        return findAll().stream()
//            .filter(userStatus -> userStatus.getUser().getId().equals(userId))
//            .findFirst();
//    }
//
//    @Override
//    public List<UserStatus> findAll() {
//        try (Stream<Path> paths = Files.list(DIRECTORY)) {
//            return paths
//                .filter(path -> path.toString().endsWith(EXTENSION))
//                .map(path -> {
//                    ReentrantLock lock = fileLockProvider.getLock(path);
//                    lock.lock();
//                    try (
//                        FileInputStream fis = new FileInputStream(path.toFile());
//                        ObjectInputStream ois = new ObjectInputStream(fis)
//                    ) {
//                        return (UserStatus) ois.readObject();
//                    } catch (IOException | ClassNotFoundException e) {
//                        throw new RuntimeException(e);
//                    } finally {
//                        lock.unlock();
//                    }
//                })
//                .toList();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
