package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDeleteRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserView;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * Legacy file-based service.
 *
 * 현재 과제 진행 흐름에서는 BasicUserService(+Repository DI)를 사용합니다.
 * 이 클래스는 컴파일을 위해 DTO 기반 UserService 인터페이스에만 맞춰 둡니다.
 */
public class FileUserService implements UserService {

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(
                "FileUserService is legacy. Use BasicUserService with repositories."
        );
    }

    @Override
    public UserView create(UserCreateRequest request) {
        throw unsupported();
    }

    @Override
    public UserView findById(UUID userId) {
        throw unsupported();
    }

    @Override
    public List<UserView> findAll() {
        throw unsupported();
    }

    @Override
    public UserView update(UserUpdateRequest request) {
        throw unsupported();
    }

    @Override
    public void delete(UserDeleteRequest request) {
        throw unsupported();
    }

    @Override
    public boolean existsById(UUID userId) {
        throw unsupported();
    }

    @Override
    public boolean existsByUsername(String username) {
        throw unsupported();
    }

    @Override
    public boolean existsByEmail(String email) {
        throw unsupported();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        throw unsupported();
    }

    @Override
    public boolean existsByDisplayName(String displayName) {
        throw unsupported();
    }
}