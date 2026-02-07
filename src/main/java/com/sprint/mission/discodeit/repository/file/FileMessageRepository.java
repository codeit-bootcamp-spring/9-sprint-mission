package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
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
public class FileMessageRepository implements MessageRepository {

    private static final String FILE_NAME = "messages.dat";
    private final File baseDir;
    private List<Message> messages;

    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String directory
    ) {
        this.baseDir = new File(directory);
        this.messages = load();
    }

    @Override
    public Message save(Message message) {
        messages.add(message);
        persist();
        return message;
    }

    @Override
    public Message find(UUID id) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        messages.removeIf(message -> message.getId().equals(id));
        persist();
    }

    private void persist() {
        ensureDirectory();
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(resolveFile()))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Message> load() {
        File file = resolveFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                List<Message> result = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof Message) {
                        result.add((Message) o);
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
