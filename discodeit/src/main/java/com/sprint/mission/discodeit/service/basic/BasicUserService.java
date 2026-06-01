package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentService binaryContentService;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtRegistry jwtRegistry;

    private final ApplicationEventPublisher eventPublisher;

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

        BinaryContentDto profileDto = profileCreateRequest
            .map(request ->{
              log.debug("프로필 이미지 업로드: fileName={}", request.fileName());
              try {
                return binaryContentService.create(request);
              } catch (Exception e) {
                log.error("프로필 이미지 저장 실패: fileName={}, error={}", request.fileName(), e.getMessage());
                throw e;
              }

            }).orElse(null);

        BinaryContent profileEntity = null;
        if (profileDto != null) {
          profileEntity = binaryContentRepository.getReferenceById(profileDto.id());
        }

        String encodedPassword = passwordEncoder.encode(userCreateRequest.password());

        User newUser = new User(
            userCreateRequest.username(),
            encodedPassword,
            userCreateRequest.email(),
            profileEntity
        );
        newUser.updateRole(Role.USER);

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
      List<User> users = userRepository.findAllWithProfile();
      List<UUID> onlineUserIds = getOnlineUserIds();
      return userMapper.toDtoList(users, onlineUserIds);
    }

    @Transactional
    @Override
    @PreAuthorize("#userId == principal.userDto.id()")
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest) {
        log.info("사용자 정보 수정 시도: id={}", userId);
        User target = userRepository.findById(userId).orElseThrow(() -> {
          log.warn("수정 실패: 존재하지 않는 사용자: id={}", userId);
          return new UserNotFoundException(userId);
        });

        BinaryContentDto profileDto = profileCreateRequest
            .map(request ->{
              log.debug("프로필 이미지 업로드: fileName={}", request.fileName());
              try {
                return binaryContentService.create(request);
              } catch (Exception e) {
                log.error("프로필 이미지 저장 실패: fileName={}, error={}", request.fileName(), e.getMessage());
                throw e;
              }

            }).orElse(null);

        BinaryContent profileEntity = null;
        if (profileDto != null) {
          profileEntity = binaryContentRepository.getReferenceById(profileDto.id());
        }
        else{
          profileEntity = target.getProfile();
        }
        target.update(userUpdateRequest.newUsername()
                , userUpdateRequest.newEmail()
                , userUpdateRequest.newPassword()
                , profileEntity);
        log.info("사용자 정보 수정 완료: id={}", userId);
        return userMapper.toDto(target);
    }

    @Transactional
    @Override
    @PreAuthorize("#userId == principal.userDto.id()")
    public void delete(UUID id) {
        log.info("사용자 삭제 시도: id={}", id);
        User removeUser = userRepository.findById(id).orElseThrow(() -> {
          log.warn("삭제 실패: 존재하지 않는 사용자: id={}", id);
          return new UserNotFoundException(id);
        });
        try {
          binaryContentRepository.deleteById(removeUser.getProfile().getId());
          userRepository.deleteById(removeUser.getId());
        } catch (Exception e) {
          log.error("사용자 삭제 중 시스템 오류 발생: userId={}, error={}", id, e.getMessage());
          throw new RuntimeException(e);
        }
    }

  @Transactional
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    user.updateRole(request.newRole());
    jwtRegistry.invalidateJwtInformationByUserId(request.userId());

    eventPublisher.publishEvent(new RoleUpdatedEvent(user.getId()));

    return userMapper.toDto(user);
  }
  public List<UUID> getOnlineUserIds() {

      return jwtRegistry.getActiveUserIds();
  }
}