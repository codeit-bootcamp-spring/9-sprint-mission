package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(MethodSecurityAuthorizationTest.Config.class)
class MethodSecurityAuthorizationTest {

  @Autowired
  private ChannelService channelService;

  @Autowired
  private UserService userService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private ChannelMapper channelMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private MessageRepository messageRepository;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @WithMockUser(authorities = "USER")
  void publicChannelCreate_WithUserAuthority_IsForbidden() {
    assertThatThrownBy(() -> channelService.create(
        new PublicChannelCreateRequest("general", "description")))
        .isInstanceOf(AccessDeniedException.class);
  }

  @Test
  @WithMockUser(authorities = "ADMIN")
  void publicChannelCreate_WithAdminAuthority_UsesHierarchy() {
    ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "general",
        "description", null, null);
    given(channelMapper.toDto(org.mockito.ArgumentMatchers.any(Channel.class)))
        .willReturn(channelDto);

    channelService.create(new PublicChannelCreateRequest("general", "description"));
  }

  @Test
  @WithMockUser(authorities = "CHANNEL_MANAGER")
  void updateRole_WithChannelManagerAuthority_IsForbidden() {
    assertThatThrownBy(() -> userService.updateRole(
        new UserRoleUpdateRequest(UUID.randomUUID(), Role.ADMIN)))
        .isInstanceOf(AccessDeniedException.class);
  }

  @Test
  @WithMockUser(authorities = "ADMIN")
  void updateRole_WithAdminAuthority_Succeeds() {
    UUID userId = UUID.randomUUID();
    User user = new User("testuser", "test@example.com", "password", null);
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true,
        Role.CHANNEL_MANAGER);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    userService.updateRole(new UserRoleUpdateRequest(userId, Role.CHANNEL_MANAGER));
  }

  @Test
  void userDelete_WithSameUser_Succeeds() {
    UUID userId = UUID.randomUUID();
    authenticateAs(userId);
    given(userRepository.existsById(userId)).willReturn(true);

    userService.delete(userId);

    verify(userRepository).deleteById(userId);
  }

  @Test
  void userUpdate_WithDifferentUser_IsForbidden() {
    authenticateAs(UUID.randomUUID());

    assertThatThrownBy(() -> userService.update(
        UUID.randomUUID(), null, Optional.empty()))
        .isInstanceOf(AccessDeniedException.class);
  }

  @Test
  void messageDelete_WithAuthor_Succeeds() {
    UUID userId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();
    authenticateAs(userId);
    given(messageRepository.findAuthorIdById(messageId)).willReturn(Optional.of(userId));
    given(messageRepository.existsById(messageId)).willReturn(true);

    messageService.delete(messageId);

    verify(messageRepository).deleteById(messageId);
  }

  @Test
  void messageUpdate_WithDifferentAuthor_IsForbidden() {
    UUID messageId = UUID.randomUUID();
    authenticateAs(UUID.randomUUID());
    given(messageRepository.findAuthorIdById(messageId)).willReturn(Optional.of(UUID.randomUUID()));

    assertThatThrownBy(() -> messageService.update(
        messageId, new MessageUpdateRequest("updated content")))
        .isInstanceOf(AccessDeniedException.class);
  }

  private void authenticateAs(UUID userId) {
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, true, Role.USER);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");
    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken(userDetails, "password", "USER"));
  }

  @TestConfiguration
  @EnableMethodSecurity
  static class Config {

    @Bean
    ChannelService channelService(ChannelRepository channelRepository,
        ReadStatusRepository readStatusRepository, MessageRepository messageRepository,
        UserRepository userRepository, ChannelMapper channelMapper) {
      return new BasicChannelService(channelRepository, readStatusRepository, messageRepository,
          userRepository, channelMapper);
    }

    @Bean
    UserService userService(UserRepository userRepository,
        UserMapper userMapper, BinaryContentRepository binaryContentRepository,
        BinaryContentStorage binaryContentStorage, PasswordEncoder passwordEncoder,
        JwtRegistry jwtRegistry) {
      return new BasicUserService(userRepository, userMapper, binaryContentRepository,
          binaryContentStorage, passwordEncoder, jwtRegistry);
    }

    @Bean
    MessageService messageService(MessageRepository messageRepository,
        ChannelRepository channelRepository, UserRepository userRepository,
        MessageMapper messageMapper, BinaryContentStorage binaryContentStorage,
        BinaryContentRepository binaryContentRepository, PageResponseMapper pageResponseMapper) {
      return new BasicMessageService(messageRepository, channelRepository, userRepository,
          messageMapper, binaryContentStorage, binaryContentRepository, pageResponseMapper);
    }

    @Bean
    ResourceOwnerAuthorization resourceOwnerAuthorization(MessageRepository messageRepository) {
      return new ResourceOwnerAuthorization(messageRepository);
    }

    @Bean
    org.springframework.security.access.hierarchicalroles.RoleHierarchy roleHierarchy() {
      return org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.fromHierarchy("""
          ADMIN > CHANNEL_MANAGER
          CHANNEL_MANAGER > USER
          """);
    }

    @Bean
    static org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
        methodSecurityExpressionHandler(
            org.springframework.security.access.hierarchicalroles.RoleHierarchy roleHierarchy) {
      org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
          handler =
          new org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler();
      handler.setRoleHierarchy(roleHierarchy);
      return handler;
    }

    @Bean
    ChannelRepository channelRepository() {
      return mock(ChannelRepository.class);
    }

    @Bean
    ReadStatusRepository readStatusRepository() {
      return mock(ReadStatusRepository.class);
    }

    @Bean
    MessageRepository messageRepository() {
      return mock(MessageRepository.class);
    }

    @Bean
    UserRepository userRepository() {
      return mock(UserRepository.class);
    }

    @Bean
    ChannelMapper channelMapper() {
      return mock(ChannelMapper.class);
    }

    @Bean
    UserMapper userMapper() {
      return mock(UserMapper.class);
    }

    @Bean
    MessageMapper messageMapper() {
      return mock(MessageMapper.class);
    }

    @Bean
    PageResponseMapper pageResponseMapper() {
      return mock(PageResponseMapper.class);
    }

    @Bean
    BinaryContentRepository binaryContentRepository() {
      return mock(BinaryContentRepository.class);
    }

    @Bean
    BinaryContentStorage binaryContentStorage() {
      return mock(BinaryContentStorage.class);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
      return mock(PasswordEncoder.class);
    }

    @Bean
    JwtRegistry jwtRegistry() {
      return mock(JwtRegistry.class);
    }
  }
}
