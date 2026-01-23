package service.file;

import entity.Channel;
import service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);}

    @Override
    public Channel create(String frame, String channelName, String detail) {
        Channel channel = new Channel(frame, channelName, detail);
        Path path = resolvePath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public Channel find(UUID id) {
        Channel channelNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try(
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ) {
                channelNullable = (Channel) ois.readObject();
            }catch (IOException | ClassNotFoundException e) {
                throw  new RuntimeException(e);
            }
        }
        return Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " bot found"));

    }

    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                                ){
                            return (Channel) ois.readObject();
                        }catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Channel> checkAll() {
        return List.of();
    }

    @Override
    public Channel update(UUID id, String frame, String channelName, String detail) {
        Channel channelNullable = null;
        Path path = resolvePath(id);
        if(Files.exists(path)) {
            try(
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ){
                channelNullable = (Channel) ois.readObject();
            }catch (IOException | ClassNotFoundException e) {
                throw  new RuntimeException(e);
            }
        }Channel channel = Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));

        try(
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
                ){
            oos.writeObject(channel);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Channel with id " + id + " not found");
        }
        try {
            Files.delete(path);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}




