package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_NAME = "channels.dat";
    private final File baseDir;
    private List<Channel> channels;

    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String directory
    ) {
        this.baseDir = new File(directory);
        this.channels = load();
    }

    @Override
    public Channel save(Channel channel) {
        channels.add(channel);
        persist();
        return channel;
    }

    @Override
    public Channel find(UUID id) {
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels);
    }

    @Override
    public void delete(UUID id) {
        channels.removeIf(channel -> channel.getId().equals(id));
        persist();
    }

    private void persist() {
        ensureDirectory();
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(resolveFile()))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Channel> load() {
        File file = resolveFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                List<Channel> result = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof Channel) {
                        result.add((Channel) o);
                    }
                }
                return result;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private File resolveFile() {
        return new File(baseDir, FILE_NAME);
    }

    private void ensureDirectory() {
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }
}
