package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  private final UserStatusService userStatusService;
  private final BinaryContentService binaryContentService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest request, BinaryContentCreateRequest profileRequest) {
    // 1. 중복 체크
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalStateException("이미 존재하는 이메일입니다.");
    }

    // 2. 프로필 이미지 엔티티 연동
    BinaryContent profile = null;
    if (profileRequest != null) {
      var profileDto = binaryContentService.create(profileRequest);
      profile = binaryContentRepository.findById(profileDto.id()).orElseThrow();
    }

    // 3. 비밀번호 암호화(멘토님 코드리뷰)
    String encodedPassword = passwordEncoder.encode(request.password());

    User user = new User(
        request.username(),
        request.email(),
        encodedPassword,
        profile
    );

    User savedUser = userRepository.save(user);

    // 5. 유저 상태 초기화 (1:1 관계)
    userStatusService.create(savedUser.getId());

    return toDtoWithOnlineStatus(savedUser);
  }

  @Override
  @Transactional
  public UserDto update(UUID id, UserUpdateRequest request,
      BinaryContentCreateRequest profileRequest) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

    // 4. API 명세 v1.1 필드명 반영 (newUsername 등)
    BinaryContent newProfile = user.getProfile();
    if (profileRequest != null) {
      if (user.getProfile() != null) {
        binaryContentService.delete(user.getProfile().getId());
      }
      var profileDto = binaryContentService.create(profileRequest);
      newProfile = binaryContentRepository.findById(profileDto.id()).orElseThrow();
    }

    user.update(request.newUsername(), request.newEmail(), request.newPassword(), newProfile);
    return toDtoWithOnlineStatus(user);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

    // [물리 자원 해제] DB 삭제 전, 로컬 스토리지의 파일만 수동 삭제 [cite: 2026-03-04]
    if (user.getProfile() != null) {
      binaryContentService.delete(user.getProfile().getId());
    }

    // 참여 채널 탈퇴 처리 (N:M 관계는 수동 정리가 안전) [cite: 2026-03-05]
    channelRepository.findAllByUserId(id).forEach(channel -> channel.removeParticipant(user));

    // [JPA 마법] user를 삭제하면 UserStatus와 BinaryContent(DB행)가 자동 삭제됨 [cite: 2026-03-05]
    userRepository.delete(user);
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(this::toDtoWithOnlineStatus)
        .toList();
  }

  @Override
  public UserDto findById(UUID id) {
    return userRepository.findById(id)
        .map(this::toDtoWithOnlineStatus)
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
  }

  // UserDto의 online 필드를 UserStatusService를 통해 채워주는 헬퍼 메서드
  private UserDto toDtoWithOnlineStatus(User user) {
    UserDto dto = userMapper.toDto(user);
    // UserStatusService에서 온라인 여부를 판별하여 주입
    return new UserDto(dto.id(), dto.username(), dto.email(), dto.profile(),
        userStatusService.isUserOnline(user.getId()));
  }

}