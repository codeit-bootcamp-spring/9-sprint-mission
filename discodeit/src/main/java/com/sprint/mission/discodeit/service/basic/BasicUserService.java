package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.PaginationMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PaginationMapper paginationMapper; // 추가됨

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .orElse(null);
        String password = userCreateRequest.password();

        User user = new User(username, email, password, nullableProfile);
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);

        userRepository.save(user);
        user.setStatus(userStatus);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<UserDto> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserDto> dtoPage = userPage.map(userMapper::toDto);
        return paginationMapper.toDto(dtoPage); // 제네릭 매퍼로 처리
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .orElse(null);

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}