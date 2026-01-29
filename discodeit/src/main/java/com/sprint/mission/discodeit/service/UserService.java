package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.DTO.UserCreatRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


public interface UserService {
    static User create(UserCreatRequest request);
    User find(UUID userId);
    List<User> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPassword);
    void delete(UUID userId);


}
