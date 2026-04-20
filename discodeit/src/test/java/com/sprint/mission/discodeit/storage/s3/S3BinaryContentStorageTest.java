package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "discodeit.storage.type=s3")
class S3BinaryContentStorageTest {

  @Autowired
  private BinaryContentStorage storage;

  @Test
  void putTest() {
    UUID id = UUID.randomUUID();
    byte[] data = "hello s3 storage".getBytes();

    UUID result = storage.put(id, data);

    assertThat(result).isEqualTo(id);
  }

  @Test
  void getTest() throws Exception {
    UUID id = UUID.randomUUID();
    byte[] data = "hello get".getBytes();

    storage.put(id, data);

    InputStream inputStream = storage.get(id);
    String content = new String(inputStream.readAllBytes());

    assertThat(content).isEqualTo("hello get");
  }

  @Test
  void downloadTest() {
    UUID id = UUID.randomUUID();
    byte[] data = "hello download".getBytes();

    storage.put(id, data);

    BinaryContentDto dto = new BinaryContentDto(
        id,
        "test.txt",
        (long) data.length,
        "text/plain"
    );

    ResponseEntity<?> response = storage.download(dto);

    assertThat(response.getStatusCode().value()).isEqualTo(302);
    assertThat(response.getHeaders().getLocation()).isNotNull();
  }
}
