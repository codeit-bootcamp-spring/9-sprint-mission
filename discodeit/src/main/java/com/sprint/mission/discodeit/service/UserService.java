package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserDeleteRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String displayName, String email, String phoneNumber);
    UserView update(UserUpdateRequest request);
    UserView findById(UUID userId);
    List<UserView> findAll();
    void delete(UserDeleteRequest request);

    //등록여부 확인
    boolean existsById(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
