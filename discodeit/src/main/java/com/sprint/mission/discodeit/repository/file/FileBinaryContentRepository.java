package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.*;
import java.util.*;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private final String filePath;
    private Map<UUID, BinaryContent> contentMap;

    public FileBinaryContentRepository(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        this.contentMap = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> loadData() {
        File file = new File(this.filePath);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileBinaryContentRepository Error] 로드 실패: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.filePath))) {
            oos.writeObject(contentMap);
        } catch (IOException e) {
            System.err.println("[FileBinaryContentRepository Error] 저장 실패: " + e.getMessage());
        }
    }

    @Override
    // void -> BinaryContent로 리턴 타입 변경
    public BinaryContent save(BinaryContent binaryContent) {
        contentMap.put(binaryContent.getId(), binaryContent);
        saveData();
        return binaryContent; // 저장된 객체를 그대로 반환 (규격 준수)
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(contentMap.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent content = contentMap.get(id);
            if (content != null) {
                result.add(content);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        if (contentMap.remove(id) != null) {
            saveData();
        }
    }
}