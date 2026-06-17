package repository.file;

import entity.User;
import repository.UserRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Paths.get(
                System.getProperty("user.dir"),
                "file-data-map",
                User.class.getSimpleName()
        );

        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        Objects.requireNonNull(id, "id is null");
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    private void write(User user) {
        Path path = resolvePath(user.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private User read(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User save(User user) {
        Objects.requireNonNull(user, "user is null");
        Objects.requireNonNull(user.getId(), "user.id is null");

        write(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (id == null) return Optional.empty();

        Path path = resolvePath(id);
        if (Files.notExists(path)) return Optional.empty();

        return Optional.of(read(path));
    }

    @Override
    public List<User> findAll() {
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
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;

        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
