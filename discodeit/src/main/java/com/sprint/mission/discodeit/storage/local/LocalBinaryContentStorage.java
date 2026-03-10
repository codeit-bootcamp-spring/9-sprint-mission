package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        try {
            Path filePath = resolvePath(binaryContentId);
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file data", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        // 5. 직접 파일을 읽지 않고, 기존에 구현한 get() 메서드를 활용하여 바이너리 데이터를 조회합니다.
        InputStream inputStream = get(metaData.id());
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metaData.fileName() + "\"")
                .contentType(MediaType.parseMediaType(metaData.contentType()))
                .body(resource);
    }
}