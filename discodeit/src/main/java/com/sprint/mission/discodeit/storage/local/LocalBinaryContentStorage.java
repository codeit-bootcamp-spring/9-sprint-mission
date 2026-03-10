package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

    // 파일을 저장할 로컬 디렉토리 경로 (프로젝트 최상단 uploads 폴더)
    private final String uploadDir = "uploads/";

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // 디렉토리가 없으면 생성
            }
            Path path = Paths.get(uploadDir + binaryContentId.toString());
            Files.write(path, bytes); // 실제 파일 시스템에 바이트 배열 저장
            return binaryContentId;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file data", e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            File file = new File(uploadDir + binaryContentId.toString());
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file data", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        try {
            Path path = Paths.get(uploadDir + metaData.id().toString());
            Resource resource = new InputStreamResource(Files.newInputStream(path));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metaData.fileName() + "\"")
                    .contentType(MediaType.parseMediaType(metaData.contentType()))
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }
}