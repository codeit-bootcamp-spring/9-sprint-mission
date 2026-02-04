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

public class FileUserService implements UserService, Serializable {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }
    private Path resolvePath(String name) {
        return DIRECTORY.resolve(name + EXTENSION);


    }
    @Override
    public void addUser(User user) {
        Path path = resolvePath(user.getUsername());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public User getUser(String username) {
        User userNullable = null;
        Path path = resolvePath(username);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream is = new ObjectInputStream(fis)
            ) {
                userNullable = (User) is.readObject();
            } catch (IOException |ClassNotFoundException e){
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with name" + username + " not found"));
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();


        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean updateUser(User user) {
        Path newpath = resolvePath(user.getUsername());

        try(
                FileOutputStream fis = new FileOutputStream(newpath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fis)
        ){
            oos.writeObject(user);
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public boolean deleteUser(String Username) {
        Path path = resolvePath(Username);
        if(Files.notExists(path)) {
            throw new NoSuchElementException("User with name" + Username + " not found");
        }
        try{
            Files.delete(path);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return true;
    }
}
