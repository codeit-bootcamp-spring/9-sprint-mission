package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "data/messages.txt";

    public FileMessageRepository() {
        try {
            Path path = Path.of(FILE_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 생성 실패", e);
        }
    }

    // 저장 포맷:
    // id|createdAt|updatedAt|content|channelId|authorId
    private List<Message> loadAll() {
        List<Message> messages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split("\\|");

                Message message = new Message(
                        UUID.fromString(parts[0]),
                        Long.parseLong(parts[1]),
                        Long.parseLong(parts[2]),
                        parts[3],
                        UUID.fromString(parts[4]),
                        UUID.fromString(parts[5])
                );

                messages.add(message);
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 읽기 실패", e);
        }

        return messages;
    }

    private void saveAll(List<Message> messages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Message m : messages) {
                writer.write(toLine(m));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 저장 실패", e);
        }
    }

    private String toLine(Message m) {
        return String.join("|",
                m.getId().toString(),
                String.valueOf(m.getCreatedAt()),
                String.valueOf(m.getUpdatedAt()),
                m.getContent(),
                m.getChannelId().toString(),
                m.getAuthorId().toString()
        );
    }

    @Override
    public void create(Message message) {
        List<Message> messages = loadAll();
        messages.add(message);
        saveAll(messages);
    }

    @Override
    public Message findById(UUID id) {
        for (Message m : loadAll()) {
            if (m.getId().equals(id)) return m;
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return loadAll();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();
        for (Message m : loadAll()) {
            if (m.getChannelId().equals(channelId)) result.add(m);
        }
        return result;
    }

    @Override
    public boolean update(UUID id, String content) {
        List<Message> messages = loadAll();

        for (Message m : messages) {
            if (m.getId().equals(id)) {
                m.update(content);
                saveAll(messages);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        List<Message> messages = loadAll();
        boolean removed = messages.removeIf(m -> m.getId().equals(id));
        if (removed) saveAll(messages);
        return removed;
    }
}

