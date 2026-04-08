package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("create - 성공: 정상적으로 사용자를 생성한다")
    void create_success() {
        // given
        UserCreateRequest request = new UserCreateRequest("testuser", "test@email.com",
            "password1234");
        User savedUser = new User("testuser", "test@email.com", "password1234", null);
        UserDto expectedDto = new UserDto(UUID.randomUUID(), "testuser", "test@email.com", null,
            true);

        given(userRepository.existsByEmail("test@email.com")).willReturn(false);
        given(userRepository.existsByUsername("testuser")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(null);
        willDoNothing().given(entityManager).flush();
        willDoNothing().given(entityManager).refresh(any());
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, Optional.empty());

        // then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("create - 실패: 이메일이 중복되면 UserAlreadyExistsException 발생")
    void create_fail_duplicateEmail() {
        // given
        UserCreateRequest request = new UserCreateRequest("testuser", "dup@email.com",
            "password1234");
        given(userRepository.existsByEmail("dup@email.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
            .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @DisplayName("update - 성공: 사용자 정보를 수정한다")
    void update_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("olduser", "old@email.com", "password1234", null);
        UserUpdateRequest request = new UserUpdateRequest("newuser", null, null);
        UserDto expectedDto = new UserDto(userId, "newuser", "old@email.com", null, true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByUsername("newuser")).willReturn(false);
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        // when
        UserDto result = userService.update(userId, request, Optional.empty());

        // then
        assertThat(result.username()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("update - 실패: 사용자가 존재하지 않으면 UserNotFoundException 발생")
    void update_fail_notFound() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("newuser", null, null);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("delete - 성공: 사용자를 삭제한다")
    void delete_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("testuser", "test@email.com", "password1234", null);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.delete(userId);

        // then
        then(userRepository).should().delete(user);
    }

    @Test
    @DisplayName("delete - 실패: 사용자가 존재하지 않으면 UserNotFoundException 발생")
    void delete_fail_notFound() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.delete(userId))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("create - 실패: 사용자명이 중복되면 UserAlreadyExistsException 발생")
    void create_fail_duplicateUsername() {
        // given
        UserCreateRequest request = new UserCreateRequest("dupuser", "test@email.com", "password1234");
        given(userRepository.existsByEmail("test@email.com")).willReturn(false);
        given(userRepository.existsByUsername("dupuser")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
            .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @DisplayName("create - 성공: 프로필 이미지와 함께 사용자를 생성한다")
    void create_success_withProfile() {
        // given
        UserCreateRequest request = new UserCreateRequest("testuser", "test@email.com", "password1234");
        byte[] bytes = "image".getBytes();
        BinaryContentCreateRequest profileReq = new BinaryContentCreateRequest("avatar.png", "image/png", bytes);
        BinaryContent profile = new BinaryContent("avatar.png", (long) bytes.length, "image/png");
        User savedUser = new User("testuser", "test@email.com", "password1234", profile);
        UserDto expectedDto = new UserDto(UUID.randomUUID(), "testuser", "test@email.com", null, true);

        given(userRepository.existsByEmail("test@email.com")).willReturn(false);
        given(userRepository.existsByUsername("testuser")).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(profile);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(null);
        willDoNothing().given(entityManager).flush();
        willDoNothing().given(entityManager).refresh(any());
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        // when
        UserDto result = userService.create(request, Optional.of(profileReq));

        // then
        assertThat(result).isNotNull();
        then(binaryContentStorage).should().put(any(), any(byte[].class));
    }

    @Test
    @DisplayName("update - 실패: 이메일이 중복되면 UserAlreadyExistsException 발생")
    void update_fail_duplicateEmail() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("olduser", "old@email.com", "password1234", null);
        UserUpdateRequest request = new UserUpdateRequest(null, "dup@email.com", null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("dup@email.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
            .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @DisplayName("update - 실패: 사용자명이 중복되면 UserAlreadyExistsException 발생")
    void update_fail_duplicateUsername() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("olduser", "old@email.com", "password1234", null);
        UserUpdateRequest request = new UserUpdateRequest("dupuser", null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByUsername("dupuser")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
            .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @DisplayName("update - 성공: 프로필 이미지를 교체한다")
    void update_success_withProfileChange() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        BinaryContent oldProfile = new BinaryContent("old.png", 100L, "image/png");
        Field idField = oldProfile.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(oldProfile, UUID.randomUUID());

        User user = new User("testuser", "test@email.com", "password1234", oldProfile);
        byte[] newBytes = "newimage".getBytes();
        BinaryContentCreateRequest profileReq = new BinaryContentCreateRequest("new.png", "image/png", newBytes);
        BinaryContent newProfile = new BinaryContent("new.png", (long) newBytes.length, "image/png");
        UserDto expectedDto = new UserDto(userId, "testuser", "test@email.com", null, true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(newProfile);
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        // when
        UserDto result = userService.update(userId, new UserUpdateRequest(null, null, null), Optional.of(profileReq));

        // then
        assertThat(result).isNotNull();
        then(binaryContentStorage).should().delete(any(UUID.class));
        then(binaryContentStorage).should().put(any(), any(byte[].class));
    }

    @Test
    @DisplayName("delete - 성공: 프로필이 있는 사용자를 삭제하면 프로필도 삭제된다")
    void delete_success_withProfile() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        BinaryContent profile = new BinaryContent("avatar.png", 100L, "image/png");
        Field idField = profile.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(profile, UUID.randomUUID());

        User user = new User("testuser", "test@email.com", "password1234", profile);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.delete(userId);

        // then
        then(binaryContentStorage).should().delete(any(UUID.class));
        then(binaryContentRepository).should().deleteById(any(UUID.class));
        then(userRepository).should().delete(user);
    }

    @Test
    @DisplayName("findAll - 성공: 전체 사용자 목록을 조회한다")
    void findAll_success() {
        // given
        User user = new User("testuser", "test@email.com", "password1234", null);
        UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@email.com", null, true);
        given(userRepository.findAllWithDetails()).willReturn(List.of(user));
        given(userMapper.toDto(user)).willReturn(userDto);

        // when
        List<UserDto> result = userService.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("testuser");
    }
}
