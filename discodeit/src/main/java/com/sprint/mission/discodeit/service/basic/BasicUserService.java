package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // [의존성] 롬복이 생성자 자동 생성 (순환 참조 방지)
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        // [검증] 이름/이메일 중복 체크
        if (userRepository.findAll().stream().anyMatch(u ->
                u.getUsername().equals(request.username()) || u.getEmail().equals(request.email()))) {
            throw new IllegalArgumentException("이미 존재하는 사용자명 또는 이메일입니다.");
        }

        // 1. 유저 저장
        User newUser = new User(request.username(), request.email(), request.password());
        userRepository.save(newUser);

        // 2. [선택] 프로필 이미지 저장 (바이너리 컨텐츠)
        if (request.profileImage() != null && request.profileImageFileName() != null) {
            BinaryContent profileImg = new BinaryContent(
                    UUID.randomUUID(),
                    request.profileImage(),
                    request.profileImageFileName(),
                    newUser.getId(), // 유저 ID 연결
                    null,
                    Instant.now()
            );
            binaryContentRepository.save(profileImg);
        }

        // 3. UserStatus 같이 생성 (가입 시 무조건 생성)
        UserStatus status = new UserStatus(
                UUID.randomUUID(),
                newUser.getId(),
                Instant.now(), // 마지막 접속: 지금
                Instant.now(),
                Instant.now()
        );
        userStatusRepository.save(status);

        return toResponse(newUser);
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return toResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 1. 정보 수정 (있는 값만)
        user.update(request.username(), request.email(), request.password());

        // 2. [선택] 프로필 이미지 대체
        if (request.profileImage() != null) {
            BinaryContent newProfileImg = new BinaryContent(
                    UUID.randomUUID(),
                    request.profileImage(),
                    request.profileImageFileName(),
                    user.getId(),
                    null,
                    Instant.now()
            );
            binaryContentRepository.save(newProfileImg);
        }

        userRepository.save(user); // 변경사항 저장
        return toResponse(user);
    }

    @Override
    public void delete(UUID userId) {
        // [삭제] 관련된 도메인(Status) 먼저 삭제
        UserStatus status = userStatusRepository.findById(userId);
        if (status != null) {
            userStatusRepository.deleteById(status.getId());
        }

        // (참고: 프로필 이미지도 지워야 하지만, BinaryContent 조회 로직이 필요해서 일단 생략하거나
        //  추후 BinaryContentRepository에 findByUserId가 생기면 추가하면 됩니다.)

        userRepository.deleteById(userId);
    }

    // [변환기] User 엔티티 -> UserResponse DTO
    private UserResponse toResponse(User user) {
        // 온라인 상태 확인
        UserStatus status = userStatusRepository.findById(user.getId());
        boolean isOnline = (status != null) && status.isOnline();

        // 비밀번호 제외하고 포장
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                isOnline,
                null // 프로필 URL은 아직 생성 로직이 없어서 null로 둡니다.
        );
    }
}