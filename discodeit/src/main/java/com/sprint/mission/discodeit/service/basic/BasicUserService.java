package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Optional<UserDto.Response> create(UserDto.CreateRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return Optional.empty();
        }
        User user = new User(request.displayName(), request.email(), request.password(), request.phoneNumber());
        user.setProfileId(request.profileId());
        userRepository.save(user);

        userStatusRepository.save(new UserStatus(user.getId()));
        return Optional.of(convertToResponse(user));
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
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userStatusRepository.findByUserId(id).ifPresent(s -> userStatusRepository.delete(s.getId()));
            if (user.getProfileId() != null) {
                binaryContentRepository.delete(user.getProfileId());
            }
            userRepository.delete(id);
            return true;
        }
        return false;
    }

    @Override public Optional<UserDto.Response> findById(UUID id) { return userRepository.findById(id).map(this::convertToResponse); }
    @Override public List<UserDto.Response> findAll() {
        List<UserDto.Response> res = new ArrayList<>();
        for (User u : userRepository.findAll()) { res.add(convertToResponse(u)); }
        return res;
    }

    private UserDto.Response convertToResponse(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId()).map(UserStatus::isOnline).orElse(false);
        return new UserDto.Response(user.getId(), user.getDisplayName(), user.getEmail(), isOnline, user.getProfileId());
    }
}