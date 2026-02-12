package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusService userStatusService;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;

    private boolean isDuplicate(String email, String phone, Predicate<User> condition) {
        return userRepository.findAll().stream()
                .filter(condition)
                .anyMatch(u -> u.getEmail().equals(email) ||
                        (u.getPhoneNumber() != null && u.getPhoneNumber().equals(phone)));
    }

    @Override
    public Optional<UserDto> create(UserCreateRequest request) {
        if (isDuplicate(request.email(), request.phoneNumber(), u -> true)) return Optional.empty();

        User user = new User(request.username(), request.email(), request.password(), request.phoneNumber(), request.profileId());
        userRepository.save(user);
        userStatusService.create(user.getId());
        return Optional.of(convertToResponse(user));
    }

    @Override
    public Optional<UserDto> update(UUID id, UserUpdateRequest request) {
        return userRepository.findById(id).map(user -> {
            if (isDuplicate(request.email(), request.phoneNumber(), u -> !u.getId().equals(id))) {
                throw new IllegalStateException("이미 사용 중인 이메일 또는 전화번호입니다.");
            }
            user.setDisplayName(request.username());
            user.setEmail(request.email());
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
            userStatusService.deleteByUserId(id);
            if (user.getProfileId() != null) binaryContentRepository.delete(user.getProfileId());
            channelRepository.findAll().forEach(channel -> {
                List<UUID> participants = channel.getParticipantUserIds();
                if (participants != null && participants.contains(id)) {
                    List<UUID> mutableParticipants = new ArrayList<>(participants);
                    mutableParticipants.remove(id);
                    channel.setParticipantUserIds(mutableParticipants);
                    channelRepository.save(channel);
                }
            });
            userRepository.delete(id);
            return true;
        }).orElse(false);
    }

    @Override
    public Optional<UserDto> findById(UUID id) {
        return userRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    private UserDto convertToResponse(User user) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDisplayName(),
                user.getEmail(),
                user.getProfileId(),
                userStatusService.isUserOnline(user.getId())
        );
    }
}