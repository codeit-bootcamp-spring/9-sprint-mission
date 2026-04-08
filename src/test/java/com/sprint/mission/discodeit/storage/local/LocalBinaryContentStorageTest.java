package com.sprint.mission.discodeit.storage.local;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.ResponseEntity;

class LocalBinaryContentStorageTest {

    @TempDir
    Path tempDir;

    private LocalBinaryContentStorage storage;

    @BeforeEach
    void setUp() {
        storage = new LocalBinaryContentStorage(tempDir.toString());
        storage.init();
    }

    @Test
    @DisplayName("파일을 저장하고 읽을 수 있다")
    void putAndGet() throws Exception {
        UUID id = UUID.randomUUID();
        byte[] data = "hello world".getBytes();

        storage.put(id, data);

        InputStream is = storage.get(id);
        assertThat(is.readAllBytes()).isEqualTo(data);
    }

    @Test
    @DisplayName("파일을 삭제할 수 있다")
    void delete() {
        UUID id = UUID.randomUUID();
        storage.put(id, "data".getBytes());

        storage.delete(id);

        assertThat(Files.exists(tempDir.resolve(id.toString()))).isFalse();
    }

    @Test
    @DisplayName("다운로드 시 ResponseEntity를 반환한다")
    void download() {
        UUID id = UUID.randomUUID();
        byte[] data = "file content".getBytes();
        storage.put(id, data);

        BinaryContentDto dto = new BinaryContentDto(id, "test.txt", (long) data.length, "text/plain");
        ResponseEntity<?> response = storage.download(dto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo("test.txt");
    }

    @Test
    @DisplayName("다운로드 시 contentType이 null이면 octet-stream을 사용한다")
    void download_nullContentType() {
        UUID id = UUID.randomUUID();
        byte[] data = "binary".getBytes();
        storage.put(id, data);

        BinaryContentDto dto = new BinaryContentDto(id, "file.bin", (long) data.length, null);
        ResponseEntity<?> response = storage.download(dto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Test
    @DisplayName("존재하지 않는 파일을 읽으면 RuntimeException이 발생한다")
    void get_nonExistent() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> storage.get(id))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("init - 디렉토리가 없으면 생성한다")
    void init_createsDirectory() {
        Path newDir = tempDir.resolve("newdir");
        LocalBinaryContentStorage newStorage = new LocalBinaryContentStorage(newDir.toString());
        newStorage.init();

        assertThat(Files.exists(newDir)).isTrue();
    }
}
