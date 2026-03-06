package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {

        if (userRepository.existsByUsername(userCreateRequest.username())){
            throw new IllegalStateException("유저 생성 실패 (이름 중복) | 유저 이름: " + userCreateRequest.username());
        }
        if (userRepository.existsByEmail(userCreateRequest.email())){
            throw new IllegalStateException("유저 생성 실패 (이메일 중복) | email: " + userCreateRequest.email());
        }

        BinaryContent profile = profileCreateRequest
            .map(request ->{
                String fileName = request.fileName();
                String contentType = request.contentType();
                byte[] bytes = request.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);

                System.out.println(binaryContent.getId());

                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;

            }).orElse(null);

        User newUser = userRepository.save(new User(userCreateRequest.username()
            , userCreateRequest.password()
            , userCreateRequest.email()
            , profile
        ));

        UserStatus newUserStatus = new UserStatus(newUser);
        newUser.updateUserState(newUserStatus);

        userStatusRepository.save(newUserStatus);

        userRepository.save(newUser);

        return userMapper.toDto(newUser);
    }

    @Override
    public UserDto find(UUID id) {
        User user = userRepository.findById(id).orElseThrow();
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest) {

        BinaryContent newProfile = profileCreateRequest
            .map(request ->{
                String fileName = request.fileName();
                String contentType = request.contentType();
                byte[] bytes = request.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;

            }).orElse(null);

        User target = userRepository.findById(userId).orElseThrow();
        target.update(userUpdateRequest.newUsername()
                , userUpdateRequest.newEmail()
                , userUpdateRequest.newPassword()
                , newProfile);
        return userMapper.toDto(target);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        User removeUser = userRepository.findById(id).orElseThrow();
        UserStatus userStatus = removeUser.getStatus();
        try {
            userStatusRepository.deleteById(userStatus.getId());
            binaryContentRepository.deleteById(removeUser.getProfile().getId());
            userRepository.deleteById(removeUser.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}