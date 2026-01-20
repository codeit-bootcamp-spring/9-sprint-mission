package service.file;

import entity.User;
import service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Path resolvePath(UUID id) {return DIRECTORY.resolve(id + EXTENSION);}

    @Override
    public User create(String userName, String email, String phoneNumber) {
        User user = new User(userName, email, phoneNumber);
        Path path = resolvePath(user.getId());
        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                ) {
            oos.writeObject(user);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public User find(UUID id) {
        User userNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try(
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                userNullable = (User) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
                throw  new RuntimeException(e);
            }
        }
        return Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));

    }

    @Override
    public User findByName(String userName) {
        return null;
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                                ){
                            return (User) ois.readObject();
                    }catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> checkAll() {
        return List.of();
    }

    @Override
    public User update(UUID id, String userName, String email, String phoneNumber) {
        User userNullable = null;
        Path path = resolvePath(id);
        if(Files.exists(path)) {
            try(
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ){
                userNullable = (User) ois.readObject();
            }catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }   //user.update(userName, email, phoneNumber);
            User user = Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));


        try( //ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))
               FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
                ){
            oos.writeObject(user);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        try {
            Files.delete(path);
        }catch (IOException e) {
            throw  new RuntimeException(e);
        }
    }
}
