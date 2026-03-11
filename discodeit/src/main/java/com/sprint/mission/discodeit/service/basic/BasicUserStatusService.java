package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 1. 트랜잭션 추가

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 적용
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional // 2. 쓰기 작업에는 별도 선언
  public UserStatus create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

    // 💡 레포지토리 메서드 명칭 수정: findByUser_Id
    if (userStatusRepository.findByUser_Id(userId).isPresent()) {
      throw new IllegalArgumentException("UserStatus already exists for user: " + userId);
    }

    UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + userStatusId));
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  @Transactional // 3. 변경 감지(Dirty Checking) 활용
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException("UserStatus not found: " + userStatusId));

    userStatus.update(request.newLastActiveAt());

    // 💡 save() 호출 삭제: @Transactional 덕분에 자동 업데이트됩니다.
    return userStatus;
  }

  @Override
  @Transactional // 4. 변경 감지(Dirty Checking) 활용
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    // 💡 레포지토리 메서드 명칭 수정: findByUser_Id
    UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
        .orElseThrow(() -> new NoSuchElementException("UserStatus not found for user: " + userId));

    userStatus.update(request.newLastActiveAt());

    // 💡 save() 호출 삭제
    return userStatus;
  }

  @Override
  @Transactional
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus not found: " + userStatusId);
    }
    userStatusRepository.deleteById(userStatusId);
  }
}