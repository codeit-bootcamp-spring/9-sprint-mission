package com.sprint.mission.discodeit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
    }
)
class DiscodeitApplicationTests {

  @Test
  void contextLoads() {
    // Simple context loading test
  }

}
