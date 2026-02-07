package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final String FILE_NAME = "binary_contents.dat";
    private final File baseDir;
    private List<BinaryContent> contents;

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String directory
    ) {
        this.baseDir = new File(directory);
        this.contents = load();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        contents.add(binaryContent);
        persist();
        return binaryContent;
    }

    @Override
    public BinaryContent find(UUID id) {
        for (BinaryContent content : contents) {
            if (content.getId().equals(id)) {
                return content;
            }
        }
        return null;
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();
        for (BinaryContent content : contents) {
            if (ids.contains(content.getId())) {
                result.add(content);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        contents.removeIf(content -> content.getId().equals(id));
        persist();
    }

    private void persist() {
        ensureDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resolveFile()))) {
            oos.writeObject(contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<BinaryContent> load() {
        File file = resolveFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                List<BinaryContent> result = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof BinaryContent) {
                        result.add((BinaryContent) o);
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
