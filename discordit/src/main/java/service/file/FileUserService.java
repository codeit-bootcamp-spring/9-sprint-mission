package service.file;

import entity.User;
import service.UserService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileUserService implements UserService {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileUserService() {
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
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    private void writeUser(User user) {
        Path path = resolvePath(user.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private User readUser(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // 생성
    @Override
    public boolean addUser(User user) {
        if (user == null) return false;

        // displayName 중복 방지
        boolean nameExists = getallUser().stream()
                .anyMatch(u -> Objects.equals(u.getDisplayName(), user.getDisplayName()));
        if (nameExists) return false;

        Path path = resolvePath(user.getId());
        if (Files.exists(path)) return false;

        writeUser(user);
        return true;
    }

    // 조회(이름)
    @Override
    public User getUser(String displayName) {
        return getallUser().stream()
                .filter(u -> Objects.equals(u.getDisplayName(), displayName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with name " + displayName + " not found"));
    }

    // 조회(String id)
    @Override
    public User getUserById(String userId) {
        final UUID uuid;
        try {
            uuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + userId, e);
        }
        return getbyId(uuid);
    }

    // 전체 조회
    @Override
    public List<User> getallUser() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(this::readUser)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 수정
    @Override
    public User updateUser(String oldName, String newName, String email, String phoneNumber) {
        User user = getUser(oldName);
        user.update(newName, email, phoneNumber);
        writeUser(user);
        return user;
    }

    // 삭제(이름)
    @Override
    public boolean deleteUser(String userName) {
        User user = getUser(userName); // 없으면 예외
        Path path = resolvePath(user.getId());

        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 의존성(UUID로 조회)
    @Override
    public User getbyId(UUID userId) {
        Path path = resolvePath(userId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        return readUser(path);
    }
}
