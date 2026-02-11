package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.CreateBinaryContentDto;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exepction.FailedCreate;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedInit;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> fileIdMap = new ConcurrentHashMap<>();
    private Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", BinaryContent.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.list(DIRECTORY).filter(path-> path.toString().endsWith(EXTENSION))
                    .map(path->{
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(binaryContent -> {
                        fileIdMap.put(binaryContent.getId(), binaryContent);
                    });
        } catch (Exception e) {
            throw new FailedInit("FileBinaryContentRepository init failed");
        }
    }

    @Override
    public UUID create(CreateBinaryContentDto requestDto) {
        AttachmentType type = requestDto.type();
        String file = requestDto.filename();
        BinaryContent binaryContent = new BinaryContent(type, file, null);
        
        Path path = resolvePath(binaryContent.getId());
        try(FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new FailedCreate("파일 생성에 실패했습니다.");
        }

        fileIdMap.put(binaryContent.getId(), binaryContent);

        return binaryContent.getId();
    }

    @Override
    public String find(UUID id) {
        try {
            return fileIdMap.get(id).toString();
        } catch (Exception e) {
            System.err.println("왠지 모르지만, 여기서 오류난다. " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> findAllByIdIn(List<UUID> ids) {
        List<String> result = new ArrayList<>();
        ids.forEach(id -> result.add(fileIdMap.get(id).toString()));
        return result;
    }

    @Override
    public boolean delete(UUID id) {
        try {
            Path path = resolvePath(id);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FailedDelete("IOException 발생");
        }

        fileIdMap.remove(id);
        return true;
    }

    @Override
    public void delete(List<UUID> ids) {
        ids.forEach(this::delete);
    }
}
