//package com.sprint.mission.discodeit.repository.file;
//
//import com.sprint.mission.discodeit.entity.BinaryContent;
//import com.sprint.mission.discodeit.repository.BinaryContentRepository;
//import java.util.Objects;
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
//public class FileBinaryContentRepository implements BinaryContentRepository {
//    private final Path DIRECTORY;
//    private final String EXTENSION = ".ser";
//    private final FileLockProvider fileLockProvider;
//
//    public FileBinaryContentRepository(
//        @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
//        FileLockProvider fileLockProvider
//    ){
//        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory,
//            BinaryContent.class.getSimpleName());
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
//
//    @Override
//    public void save(BinaryContent binaryContent) {
//        Path path = resolvePath(binaryContent.getId());
//        ReentrantLock lock = fileLockProvider.getLock(path);
//        lock.lock();
//        try (
//                FileOutputStream fos = new FileOutputStream(path.toFile());
//                ObjectOutputStream oos = new ObjectOutputStream(fos)
//        ) {
//            oos.writeObject(binaryContent);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }finally {
//            lock.unlock();
//        }
//    }
//
//    @Override
//    public boolean delete(UUID id) {
//        Path path = resolvePath(id);
//        if (Files.notExists(path)) {
//            throw new NoSuchElementException("BinaryContent with id " + id + " not found");
//        }
//        try {
//            Files.delete(path);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("BinaryContent 삭제 - ID: " + id);
//        return true;
//    }
//
//    @Override
//    public Optional<BinaryContent> findByID(UUID id) {
//        BinaryContent binaryContent = null;
//        Path path = resolvePath(id);
//        ReentrantLock lock = fileLockProvider.getLock(path);
//        lock.lock();
//        if (Files.exists(path)) {
//            try (
//                    FileInputStream fis = new FileInputStream(path.toFile());
//                    ObjectInputStream ois = new ObjectInputStream(fis)
//            ) {
//                binaryContent = (BinaryContent) ois.readObject();
//            } catch (IOException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }finally {
//                lock.unlock();
//            }
//        }
//        return Optional.ofNullable(binaryContent);
//    }
//
//    @Override
//    public List<BinaryContent> findAll() {
//        try (Stream<Path> paths = Files.list(DIRECTORY)) {
//            return paths
//                    .filter(path -> path.toString().endsWith(EXTENSION))
//                    .map(path -> {
//                        ReentrantLock lock = fileLockProvider.getLock(path);
//                        lock.lock();
//                        try (
//                                FileInputStream fis = new FileInputStream(path.toFile());
//                                ObjectInputStream ois = new ObjectInputStream(fis)
//                        ) {
//                            return (BinaryContent) ois.readObject();
//                        } catch (IOException | ClassNotFoundException e) {
//                            throw new RuntimeException(e);
//                        } finally {
//                            lock.unlock();
//                        }
//                    })
//                    .toList();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}