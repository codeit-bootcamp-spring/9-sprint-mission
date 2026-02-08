package com.sprint.mission.discodeit.repository.file;

import org.springframework.stereotype.Repository;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import com.sprint.mission.discodeit.entity.Channel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
     this.DIRECTORY = Path.of(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
     if (Files.notExists(DIRECTORY)){
         try {
             Files.createDirectories(DIRECTORY);
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = resolvePath(channel.getChannelId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;

    }

    @Override
    public Optional<Channel> findByChannelId(UUID channelId) {
       Channel channelNullable = null;
       Path path = resolvePath(channelId);
       if (Files.exists(path)) {
           try (
                   FileInputStream fis = new FileInputStream(path.toFile());
                   ObjectInputStream ois = new ObjectInputStream(fis)
           ){
               channelNullable = (Channel) ois.readObject();
           }catch (IOException | ClassNotFoundException e) {
               throw new RuntimeException(e);
           }
       }
       return Optional.ofNullable(Optional.ofNullable(channelNullable)
               .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + "not founded")));
    }

    @Override
    public List<Channel> findAllChannel() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID channelId) {
        Channel channelNullable = null;
        Path path = resolvePath(channelId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ){
                channelNullable = (Channel) ois.readObject();
            }catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return channelNullable != null;
    }

    @Override
    public void deleteById(UUID channelId) {
        Path path = resolvePath(channelId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
