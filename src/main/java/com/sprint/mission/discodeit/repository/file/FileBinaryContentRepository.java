package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.UUID;

@Repository
@Profile("file")
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final File file;
    private final Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository() {
        Path directory = Path.of(System.getProperty("user.dir"), "file-data", "binary-content");
        File dir = directory.toFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("binary-content 디렉토리 생성 실패: " + dir.getAbsolutePath());
        }
        this.file = directory.resolve("binary-contents.ser").toFile();
        this.data = load();
    }

    @Override
    public BinaryContent save(BinaryContent content) {
        data.put(content.getId(), content);
        persist();
        return content;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent binaryContent = data.get(id);
            if (binaryContent != null) result.add(binaryContent);
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        persist();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> load() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("binary-content 로딩 실패", e);
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("binary-content 저장 실패", e);
        }
    }
}
