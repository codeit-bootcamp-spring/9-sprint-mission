package repository.file;

import entity.Channel;
import repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static final Path DIRECTORY = Paths.get("file-data","users");

    public FileChannelRepository() {
        try{
            Files.createDirectories(DIRECTORY) ;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path path(UUID id) {return DIRECTORY.resolve(id + ".ser");}

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(path(id));
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(path(id));
    }

    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .map(p -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
                            return (Channel) ois.readObject();
                        }catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Optional<Channel> findById(UUID id) {
        Path path = path(id);
        if(Files.notExists(path)) return Optional.empty();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.of((Channel) ois.readObject());
        }catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public Channel save(Channel channel) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path(channel.getId()).toFile()))) {
            oos.writeObject(channel);
            return channel;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
