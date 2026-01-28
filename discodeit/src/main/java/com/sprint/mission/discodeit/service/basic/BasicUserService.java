package com.sprint.mission.discodeit.service.basic;

// [주의] 이 import들이 모두 본인 프로젝트 경로와 맞아야 합니다.
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Optional<UserDto.Response> create(UserDto.CreateRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("이미 존재하는 이메일입니다: {}", request.email());
            return Optional.empty();
        }

        User user = new User(request.displayName(), request.email(), request.password(), request.phoneNumber());
        user.setProfileId(request.profileId());
        userRepository.save(user);

        // 유저 상태(UserStatus) 동시 생성
        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return Optional.of(convertToResponse(user));
    }

    @Override
    public Optional<UserDto.Response> findById(UUID id) {
        return userRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto.Response> update(UUID id, UserDto.UpdateRequest request) {
        return userRepository.findById(id).map(user -> {
            user.setDisplayName(request.displayName());
            user.setPhoneNumber(request.phoneNumber());
            user.setProfileId(request.profileId());
            user.recordUpdate();
            userRepository.save(user);
            return convertToResponse(user);
        });
    }

    @Override
    public boolean delete(UUID id) {
        return userRepository.findById(id).map(user -> {
            // 연쇄 삭제 로직
            userStatusRepository.findByUserId(id).ifPresent(s -> userStatusRepository.delete(s.getId()));
            if (user.getProfileId() != null) {
                binaryContentRepository.delete(user.getProfileId());
            }
            userRepository.delete(id);
            return true;
        }).orElse(false);
    }

    // [빨간줄 박멸의 핵심] 엔티티를 DTO로 변환하는 헬퍼 메서드
    private UserDto.Response convertToResponse(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserDto.Response(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                isOnline,
                user.getProfileId()
        );
    }
}