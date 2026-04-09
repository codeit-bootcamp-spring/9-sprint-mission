package com.sprint.mission.discodeit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // [추가] 이걸 넣어야 application-test.yml을 읽습니다.
class DiscodeitApplicationTests {
  @Test
  void contextLoads() { }
}