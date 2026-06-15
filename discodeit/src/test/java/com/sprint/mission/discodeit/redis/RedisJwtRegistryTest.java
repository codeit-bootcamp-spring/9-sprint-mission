package com.sprint.mission.discodeit.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class RedisJwtRegistryTest {

  private static final String SECRET = "test-jwt-secret-key-for-mission-12-provider";

  private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
  private final Map<String, Object> redis = new ConcurrentHashMap<>();
  private RedisJwtRegistry jwtRegistry;

  @BeforeEach
  void setUp() {
    RedisTemplate<String, Object> redisTemplate = mock();
    ValueOperations<String, Object> valueOperations = mock();

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(anyString()))
        .thenAnswer(invocation -> redis.get(invocation.getArgument(0, String.class)));
    when(valueOperations.setIfAbsent(anyString(), any(), any(Duration.class)))
        .thenAnswer(invocation -> redis.putIfAbsent(invocation.getArgument(0, String.class),
            invocation.getArgument(1)) == null);
    doAnswer(invocation -> {
      redis.put(invocation.getArgument(0, String.class), invocation.getArgument(1));
      return null;
    }).when(valueOperations).set(anyString(), any(), any(Duration.class));
    when(redisTemplate.delete(anyString()))
        .thenAnswer(invocation -> redis.remove(invocation.getArgument(0, String.class)) != null);

    RedisLockProvider redisLockProvider = new RedisLockProvider(redisTemplate);
    jwtRegistry = new RedisJwtRegistry(redisTemplate, jwtTokenProvider, redisLockProvider);
  }

  @Test
  void registerJwtInformation_LimitsSameUserToOneActiveJwtInformation() {
    UserDto userDto = userDto();
    JwtInformation first = jwtInformation(userDto, "access-1", "refresh-1");
    JwtInformation second = jwtInformation(userDto, "access-2", "refresh-2");

    jwtRegistry.registerJwtInformation(first);
    jwtRegistry.registerJwtInformation(second);

    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken(first.accessToken())).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken(second.accessToken())).isTrue();
    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())).isTrue();
  }

  @Test
  void invalidateJwtInformationByUserId_RemovesUserJwtInformationFromAllIndexes() {
    UserDto userDto = userDto();
    JwtInformation jwtInformation = jwtInformation(userDto, "access", "refresh");
    jwtRegistry.registerJwtInformation(jwtInformation);

    jwtRegistry.invalidateJwtInformationByUserId(userDto.id());

    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken(jwtInformation.accessToken()))
        .isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken(jwtInformation.refreshToken()))
        .isFalse();
  }

  @Test
  void rotateJwtInformation_ReplacesAccessTokenAndRefreshToken() {
    UserDto userDto = userDto();
    JwtInformation jwtInformation = jwtInformation(userDto, "access", "refresh");
    String newAccessToken = "new-access";
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDto);
    jwtRegistry.registerJwtInformation(jwtInformation);

    JwtInformation rotated = jwtRegistry.rotateJwtInformation(
        jwtInformation.refreshToken(), newAccessToken, newRefreshToken);

    assertThat(rotated.accessToken()).isEqualTo(newAccessToken);
    assertThat(rotated.refreshToken()).isEqualTo(newRefreshToken);
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken(jwtInformation.refreshToken()))
        .isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken(newRefreshToken)).isTrue();
  }

  @Test
  void registerJwtInformation_DoesNotStoreExpiredJwtInformation() {
    UserDto userDto = userDto();
    JwtInformation expiredJwtInformation = new JwtInformation(
        userDto,
        "expired-access",
        "expired-refresh",
        Instant.now().minusSeconds(1)
    );

    jwtRegistry.registerJwtInformation(expiredJwtInformation);

    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken(expiredJwtInformation.accessToken()))
        .isFalse();
  }

  private JwtInformation jwtInformation(UserDto userDto, String accessToken, String refreshToken) {
    return new JwtInformation(
        userDto,
        accessToken,
        refreshToken,
        Instant.now().plusSeconds(3600)
    );
  }

  private UserDto userDto() {
    return new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true, Role.USER);
  }
}
