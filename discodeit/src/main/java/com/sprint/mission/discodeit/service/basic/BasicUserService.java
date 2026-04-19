package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {

        log.info("사용자 생성 시도: username={}, email={}", userCreateRequest.username(), userCreateRequest.email());
        if (userRepository.existsByUsername(userCreateRequest.username())){
          log.warn("사용자 생성 실패 (이름 중복): username={}", userCreateRequest.username());
          throw UserAlreadyExistsException.username(userCreateRequest.username());
        }
        if (userRepository.existsByEmail(userCreateRequest.email())){
          log.warn("사용자 생성 실패 (이메일 중복): email={}", userCreateRequest.email());
          throw UserAlreadyExistsException.email(userCreateRequest.email());
        }

        BinaryContent profile = profileCreateRequest
            .map(request ->{
              log.debug("프로필 이미지 업로드: fileName={}", request.fileName());
              try {
                BinaryContent binaryContent = new BinaryContent(request.fileName(), (long) request.bytes().length, request.contentType());
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), request.bytes());
                return binaryContent;
              } catch (Exception e) {
                log.error("프로필 이미지 저장 실패: fileName={}, error={}", request.fileName(), e.getMessage());
                throw e;
              }

            }).orElse(null);

        User newUser = userRepository.save(new User(userCreateRequest.username()
            , userCreateRequest.password()
            , userCreateRequest.email()
            , profile
        ));

        UserStatus newUserStatus = new UserStatus(newUser);
        newUser.updateUserState(newUserStatus);

        userRepository.save(newUser);

        log.info("사용자 생성 완료: id={}, username={}, email={}", newUser.getId(), newUser.getUsername(), newUser.getEmail());
        return userMapper.toDto(newUser);
    }

    @Override
    public UserDto find(UUID id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new UserNotFoundException(id)
        );
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAllWithStatusAndProfile().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest) {
        log.info("사용자 정보 수정 시도: id={}", userId);
        User target = userRepository.findById(userId).orElseThrow(() -> {
          log.warn("수정 실패: 존재하지 않는 사용자: id={}", userId);
          return new UserNotFoundException(userId);
        });

        BinaryContent newProfile = profileCreateRequest
            .map(request ->{
              try {
                String fileName = request.fileName();
                String contentType = request.contentType();
                byte[] bytes = request.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
              }
              catch (Exception e) {
                log.error("새 프로필 이미지 저장 실패: fileName={}, error={}", request.fileName(), e.getMessage());
                throw e;
              }

            }).orElse(target.getProfile());
        target.update(userUpdateRequest.newUsername()
                , userUpdateRequest.newEmail()
                , userUpdateRequest.newPassword()
                , newProfile);
        log.info("사용자 정보 수정 완료: id={}", userId);
        return userMapper.toDto(target);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        log.info("사용자 삭제 시도: id={}", id);
        User removeUser = userRepository.findById(id).orElseThrow(() -> {
          log.warn("삭제 실패: 존재하지 않는 사용자: id={}", id);
          return new UserNotFoundException(id);
        });
        UserStatus userStatus = removeUser.getStatus();
        try {
          userStatusRepository.deleteById(userStatus.getId());
          binaryContentRepository.deleteById(removeUser.getProfile().getId());
          userRepository.deleteById(removeUser.getId());
        } catch (Exception e) {
          log.error("사용자 삭제 중 시스템 오류 발생: userId={}, error={}", id, e.getMessage());
          throw new RuntimeException(e);
        }
    }
}