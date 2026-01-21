package repository.file;

import entity.Message;
import repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static final Path DIRECTORY = Paths.get("file-data", "users");

    public FileMessageRepository() {
        try{
            Files.createDirectories(DIRECTORY);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path path(UUID id) {return DIRECTORY.resolve(id + ".ser");}

    @Override
    public void deleteById(UUID id) {
        try{
            Files.deleteIfExists(path(id));
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(path(id));
    }

    @Override
    public List<Message> findAll() {
        try{
            return Files.list(DIRECTORY)
                    .map(p -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
                            return (Message) ois.readObject();
                        }catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path path = path(id);
        if (Files.notExists(path)) return Optional.empty();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.of((Message) ois.readObject());
        }catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message save(Message message) {
       try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path(message.getId()).toFile()))) {
           oos.writeObject(message);
           return message;
       }catch (IOException e) {
           throw new RuntimeException(e);
       }
    }
}
