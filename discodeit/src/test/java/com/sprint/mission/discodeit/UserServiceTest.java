package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void create_find_update_delete_flow() {
        // create
        UserView user = userService.create(new UserCreateRequest(
                new UserParams(
                        "seongjun",
                        "Seongjun",
                        "seongjun@test.com",
                        "010-1234-5678",
                        "22"
                ),
                null
        ));
        assertNotNull(user.id());

        // find
        UserView found = userService.findById(user.id());
        assertEquals("Seongjun", found.displayName());

        // update
        userService.update(new UserUpdateRequest(
                user.id(),
                new UserUpdateRequest.Params(
                        new UserUpdateRequest.UserFields(
                                "SJ",
                                "sj@test.com",
                                "010-0000-0000"
                        ),
                        null
                )
        ));
        UserView updated = userService.findById(user.id());
        assertEquals("SJ", updated.displayName());
        assertEquals("sj@test.com", updated.email());

        // delete
        userService.delete(new UserDeleteRequest(user.id()));
        assertFalse(userService.existsById(user.id()));
    }

    @Test
    void create_duplicate_username_throws() {
        userService.create(new UserCreateRequest(
                new UserParams(
                        "dup",
                        "A",
                        "a@test.com",
                        "010-1111-1111",
                        "1"
                ),
                null
        ));

        assertThrows(IllegalArgumentException.class, () ->
                userService.create(new UserCreateRequest(
                        new UserParams(
                                "dup",
                                "B",
                                "b@test.com",
                                "010-2222-2222",
                                "1"
                        ),
                        null
                ))
        );
    }
}