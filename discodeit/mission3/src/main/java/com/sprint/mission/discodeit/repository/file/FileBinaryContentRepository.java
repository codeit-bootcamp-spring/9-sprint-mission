package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository() {
        this.DIRECTORY = Paths.get("data", "binary");
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("저장소 디렉토리 생성 실패", e);
            }
        }
    }

    // 팩트: 이제 파일명은 ID로만 결정됩니다. ownerId는 필요 없습니다.
    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = resolvePath(binaryContent.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(binaryContent);
            return binaryContent;
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 저장 실패", e);
        }
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            return Optional.empty();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            return Optional.of((BinaryContent) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("BinaryContent 조회 실패 (ID: " + id + ")", e);
        }
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return ids.stream()
                .map(id -> {
                    Path path = resolvePath(id); // ID로 파일 경로 계산
                    if (Files.notExists(path)) {
                        return null; // 파일이 없으면 무시
                    }

                    try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
                        return (BinaryContent) ois.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        // 로그를 남기거나 예외 처리를 해야 하지만,
                        // 일단 흐름을 위해 null을 반환하고 나중에 필터링합니다.
                        return null;
                    }
                })
                .filter(Objects::nonNull) // 읽기에 성공한 객체만 리스트로 변환
                .toList();
    }
    @Override
    public boolean existById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("BinaryContent 삭제 실패 (ID: " + id + ")", e);
        }
    }

}