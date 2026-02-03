package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.status.BinaryContentInterface;
import com.sprint.mission.discodeit.status.adds.BinaryContent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileBinaryContentRepository implements BinaryContentInterface {
    private final File file = new File("binary content");
    private List<BinaryContent> load() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (List<BinaryContent>) objectInputStream.readObject();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void saveAll(List<BinaryContent> contents) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectOutputStream.writeObject(contents);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteById(UUID id) {
        List<BinaryContent> contents = load();
        contents.removeIf(binaryContent -> binaryContent.getId().equals(id));
                saveAll(contents);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return load().stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .toList();
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return load().stream()
                .filter(binaryContent -> binaryContent.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        List<BinaryContent> contents = load();
        contents.removeIf(content -> content.getId().equals(binaryContent.getId()));
        contents.add(binaryContent);
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        List<BinaryContent> contents = load();
        contents.removeIf(binaryContent -> binaryContent.getId().equals(messageId));
        saveAll(contents);
    }
}
