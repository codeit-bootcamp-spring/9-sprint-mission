package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final SessionRegistry sessionRegistry;

  @Override
  @Transactional
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new RuntimeException("User not found"));
    user.updateRole(request.newRole());

    sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(principal -> (DiscodeitUserDetails) principal)
        .filter(userDetails -> userDetails.getUserDto().id().equals(user.getId()))
        .forEach(userDetails ->
            sessionRegistry.getAllSessions(userDetails, false)
                .forEach(SessionInformation::expireNow)
        );

    return toDtoWithOnlineStatus(user);
  }

//  private UserDto toDtoWithOnlineStatus(User user) {
//    boolean isOnline = sessionRegistry.getAllPrincipals().stream()
//        .filter(principal -> principal instanceof DiscodeitUserDetails)
//        .map(principal -> (DiscodeitUserDetails) principal)
//        .anyMatch(details -> details.getUserDto().id().equals(user.getId()));
//
//    UserDto dto = UserDto.from(user);
//    return new UserDto(
//        dto.id(),
//        dto.username(),
//        dto.email(),
//        dto.profile(),
//        isOnline,
//        dto.role()
//    );
//  }

  public boolean isUserOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(principal -> (DiscodeitUserDetails) principal)
        .anyMatch(details -> details.getUserDto().id().equals(userId));
  }

  private UserDto toDtoWithOnlineStatus(User user) {
    UserDto dto = UserDto.from(user);
    return new UserDto(
        dto.id(),
        dto.username(),
        dto.email(),
        dto.profile(),
        isUserOnline(user.getId()),
        dto.role()
    );
  }

  public List<UserDto> getAllUsersWithStatus() {
    Set<UUID> onlineUserIds = sessionRegistry.getAllPrincipals().stream()
        .filter(p -> p instanceof DiscodeitUserDetails)
        .map(p -> ((DiscodeitUserDetails) p).getUserDto().id())
        .collect(Collectors.toSet());

    return userRepository.findAll().stream()
        .map(user -> {
          UserDto dto = UserDto.from(user);
          return new UserDto(
              dto.id(), dto.username(), dto.email(), dto.profile(),
              onlineUserIds.contains(user.getId()),
              dto.role()
          );
        })
        .toList();
  }
}