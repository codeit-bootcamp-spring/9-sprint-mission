package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InitialAdminRoleChangeNotAllowedException;
import com.sprint.mission.discodeit.exception.user.SelfRoleChangeNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final PasswordEncoder passwordEncoder;

  @Value("${discodeit.admin.username:admin}")
  private String adminUsername;

  @Value("${discodeit.admin.email:admin@discodeit.local}")
  private String adminEmail;

  @Transactional
  @Override
  public UserResponse create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    log.debug("Create user requested: username={}, email={}", username, email);

    validateDuplicateEmail(email);
    validateDuplicateUsername(username);

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(this::saveBinaryContent)
        .orElse(null);
    String password = passwordEncoder.encode(userCreateRequest.password());

    User user = new User(username, email, password, nullableProfile);

    User createdUser = userRepository.save(user);
    log.info("User created: userId={}, username={}",
        createdUser.getId(), createdUser.getUsername());
    return userMapper.toResponse(createdUser);
  }

  @Override
  public UserResponse find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toResponse)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
  }

  @Override
  public List<UserResponse> findAll() {
    return userRepository.findAllWithProfile()
        .stream()
        .map(userMapper::toResponse)
        .toList();
  }

  @Transactional
  @Override
  @PreAuthorize("@userAccessGuard.isSelf(#p0, authentication)")
  public UserResponse update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("Update user requested: userId={}, newUsername={}, newEmail={}",
        userId, userUpdateRequest.newUsername(), userUpdateRequest.newEmail());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    String username = userUpdateRequest.newUsername();
    String email = userUpdateRequest.newEmail();
    validateDuplicateEmail(email, user);
    validateDuplicateUsername(username, user);

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(this::saveBinaryContent)
        .orElse(null);

    String password = userUpdateRequest.newPassword();
    user.update(username, email, password, nullableProfile);

    log.info("User updated: userId={}", user.getId());

    return userMapper.toResponse(user);
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse updateRole(UserRoleUpdateRequest request) {
    UUID userId = request.userId();
    log.debug("Update user role requested: userId={}, role={}", userId, request.role());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    boolean roleChanged = request.role() != null && request.role() != user.getRole();
    validateRoleChangeAllowed(userId, user, roleChanged);

    user.updateRole(request.role());
    log.info("User role updated: userId={}, role={}", user.getId(), user.getRole());

    return userMapper.toResponse(user);
  }

  @Transactional
  @Override
  @PreAuthorize("@userAccessGuard.isSelf(#p0, authentication)")
  public void delete(UUID userId) {
    log.debug("Delete user requested: userId={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    userRepository.delete(user);
    log.info("User deleted: userId={}", userId);
  }

  private void validateDuplicateEmail(String email) {
    if (email != null && userRepository.existsByEmail(email)) {
      log.warn("Duplicate email detected: email={}", email);
      throw new UserAlreadyExistException(Map.of("email", email));
    }
  }

  private void validateDuplicateEmail(String email, User user) {
    if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
      log.warn("Duplicate email detected on update: userId={}, email={}", user.getId(), email);
      throw new UserAlreadyExistException(Map.of("email", email));
    }
  }

  private void validateDuplicateUsername(String username) {
    if (username != null && userRepository.existsByUsername(username)) {
      log.warn("Duplicate username detected: username={}", username);
      throw new UserAlreadyExistException(Map.of("username", username));
    }
  }

  private void validateDuplicateUsername(String username, User user) {
    if (username != null && !username.equals(user.getUsername())
        && userRepository.existsByUsername(username)) {
      log.warn("Duplicate username detected on update: userId={}, username={}",
          user.getId(), username);
      throw new UserAlreadyExistException(Map.of("username", username));
    }
  }

  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    byte[] bytes = request.bytes();
    log.debug("Upload profile requested: fileName={}, contentType={}, size={}",
        request.fileName(), request.contentType(), bytes.length);

    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        (long) bytes.length,
        request.contentType()
    );
    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);

    try {
      binaryContentStorage.put(createdBinaryContent.getId(), bytes);
    } catch (RuntimeException ex) {
      log.error("Profile upload failed: binaryContentId={}, fileName={}, contentType={}",
          createdBinaryContent.getId(), request.fileName(), request.contentType(), ex);
      throw ex;
    }
    log.info("Profile uploaded: binaryContentId={}, fileName={}, size={}",
        createdBinaryContent.getId(), createdBinaryContent.getFileName(),
        createdBinaryContent.getSize()
    );
    return createdBinaryContent;
  }

  private void validateRoleChangeAllowed(UUID userId, User user, boolean roleChanged) {
    if (!roleChanged) {
      return;
    }

    if (isCurrentUser(userId)) {
      throw new SelfRoleChangeNotAllowedException(Map.of("userId", userId));
    }

    if (isInitialAdminAccount(user)) {
      throw new InitialAdminRoleChangeNotAllowedException(Map.of(
          "userId", userId,
          "username", user.getUsername()
      ));
    }
  }

  private boolean isCurrentUser(UUID userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      return false;
    }
    return userDetails.getUserDto().id().equals(userId);
  }

  private boolean isInitialAdminAccount(User user) {
    return (adminUsername != null && adminUsername.equals(user.getUsername()))
        || (adminEmail != null && adminEmail.equals(user.getEmail()));
  }
}
