package repository.file;

import entity.User;
import repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {

    private final Path directory;
    private static final String EXT = ".ser";

    public FileUserRepository() {
        this.directory = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                User.class.getSimpleName()
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
    public User save(User user) {
        ensureDirectory();
        write(resolvePath(user.getId()), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        if (userId == null) return Optional.empty();
        Path path = resolvePath(userId);
        if (Files.notExists(path)) return Optional.empty();
        return Optional.of(read(path));
    }

    @Override
    public List<User> findAll() {
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
    public void deleteById(UUID userId) {
        if (userId == null) return;
        Path path = resolvePath(userId);
        if (Files.notExists(path)) return;

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + path, e);
        }
    }

    @Override
    public boolean existsById(UUID userId) {
        if (userId == null) return false;
        return Files.exists(resolvePath(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        // 파일 기반이라 인덱스가 없으면 전체 스캔
        return findAll().stream().anyMatch(u -> email.equals(u.getEmail()));
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        return findAll().stream().anyMatch(u -> phoneNumber.equals(u.getPhoneNumber()));
    }

    private User read(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read: " + path, e);
        }
    }

    private void write(Path path, User user) {
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write: " + path, e);
        }
    }
}