package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public UUID usernameToId(String name) {
    return userRepository.usernameToId(name);
  }

  @Override
  public boolean isPresent(UUID id) {
    return userRepository.isPresent(id);
  }

  @Override
  public boolean isInvalid(UUID userId, String password) {
    return userRepository.checkInvalid(userId, password);
  }

  @Override
  public ResponseUserDto create(RequestCreateUserDto requestDto, UUID profileId) {
    userRepository.duplicateChecker("사용자명", requestDto.username());
    userRepository.duplicateChecker("이메일", requestDto.email());

    return userRepository.create(requestDto, profileId);
  }

  /// Update
  @Override
  public ResponseUserDto update(UUID userId, RequestUpdateUserDto requestDto, UUID profileId) {
    if (!userRepository.isPresent(userId)) {
      throw new NotFound("해당 사용자가 존재하지 않습니다.");
    }

    userRepository.duplicateChecker("사용자명", requestDto.newUsername());
    userRepository.duplicateChecker("이메일", requestDto.newEmail());

    if (profileId != null) {
      binaryContentRepository.delete(userId);
    }
    return userRepository.update(userId, requestDto, profileId);
  }

  /// Read
  @Override
  public ResponseUserDto find(String name) {
    return userRepository.find(name);
  }

  @Override
  public ResponseUserDto find(UUID id) {
    return userRepository.find(id);
  }

  @Override
  public List<ResponseUserDto> findAll() {
    return userRepository.findAll();
  }

  /// Delete
  @Override
  public boolean delete(UUID id) {
    binaryContentRepository.delete(id);
    return userRepository.delete(id);
  }
}
