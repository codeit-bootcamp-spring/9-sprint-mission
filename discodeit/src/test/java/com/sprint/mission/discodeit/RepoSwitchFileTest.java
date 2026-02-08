package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "discodeit.repository.type=file")
class RepoSwitchFileTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void userRepo_is_file() {
        assertTrue(userRepository.getClass().getSimpleName().startsWith("File"));
    }
}