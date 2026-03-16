package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException; // [추가] 예외 처리를 위한 임포트
import java.util.UUID;

@Component
// 1. application.yaml의 discodeit.storage.type 값이 local일 때만 이 클래스를 Bean으로 등록합니다.
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    // 2. application.yaml에 정의된 root-path 값을 주입받아 초기화합니다.
    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    // 3. Bean이 생성될 때 자동으로 한 번 호출되어 디렉토리를 초기화(생성)합니다.
    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize root directory for storage", e);
        }
    }

    // 4. UUID 키 정보를 바탕으로 일관된 파일 저장 위치 규칙(root/UUID)을 반환합니다.
    private Path resolvePath(UUID binaryContentId) {
        return root.resolve(binaryContentId.toString());
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            Path filePath = resolvePath(binaryContentId);
            Files.write(filePath, bytes);
            return binaryContentId;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file data", e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path filePath = resolvePath(binaryContentId);

        // [추가된 방어 로직] 파일이 물리적으로 존재하지 않으면 404를 유도하기 위해 예외 던짐
        if (!Files.exists(filePath)) {
            throw new NoSuchElementException("요청한 파일이 서버에 존재하지 않습니다: " + binaryContentId);
        }

        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file data", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        InputStream inputStream = get(metaData.id());
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metaData.fileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, metaData.contentType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size()))
                .body(resource);
    }
}