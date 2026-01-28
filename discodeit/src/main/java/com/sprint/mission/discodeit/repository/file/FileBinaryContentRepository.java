package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.util.*;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final String FILE_PATH = "binary_content.ser";
    private Map<UUID, BinaryContent> contentMap;

    public FileBinaryContentRepository() {
        this.contentMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(contentMap);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void save(BinaryContent binaryContent) {
        contentMap.put(binaryContent.getId(), binaryContent);
        saveData();
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(contentMap.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return contentMap.values().stream()
                .filter(content -> ids.contains(content.getId()))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (contentMap.remove(id) != null) saveData();
    }
}