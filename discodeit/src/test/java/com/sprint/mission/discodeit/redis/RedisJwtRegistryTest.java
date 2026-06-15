package com.sprint.mission.discodeit.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

class RedisJwtRegistryTest {

  private static final String SECRET = "test-jwt-secret-key-for-mission-12-provider";

  private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
  private final Map<String, Object> redis = new ConcurrentHashMap<>();
  private final Map<String, List<Object>> lists = new ConcurrentHashMap<>();
  private final Map<String, Set<Object>> sets = new ConcurrentHashMap<>();
  private RedisJwtRegistry jwtRegistry;

  @BeforeEach
  void setUp() {
    RedisTemplate<String, Object> redisTemplate = mock();
    ValueOperations<String, Object> valueOperations = mock();
    ListOperations<String, Object> listOperations = mock();
    SetOperations<String, Object> setOperations = mock();

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(redisTemplate.opsForList()).thenReturn(listOperations);
    when(redisTemplate.opsForSet()).thenReturn(setOperations);
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
        .thenAnswer(invocation -> {
          String key = invocation.getArgument(0, String.class);
          boolean removed = redis.remove(key) != null;
          removed = lists.remove(key) != null || removed;
          removed = sets.remove(key) != null || removed;
          return removed;
        });
    when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);
    when(redisTemplate.keys(anyString()))
        .thenAnswer(invocation -> {
          String pattern = invocation.getArgument(0, String.class).replace("*", "");
          Set<String> keys = new HashSet<>();
          lists.keySet().stream()
              .filter(key -> key.startsWith(pattern))
              .forEach(keys::add);
          return keys;
        });

    when(listOperations.size(anyString()))
        .thenAnswer(invocation -> Long.valueOf(
            list(invocation.getArgument(0, String.class)).size()));
    when(listOperations.rightPush(anyString(), any()))
        .thenAnswer(invocation -> {
          List<Object> values = list(invocation.getArgument(0, String.class));
          values.add(invocation.getArgument(1));
          return (long) values.size();
        });
    when(listOperations.leftPop(anyString()))
        .thenAnswer(invocation -> {
          List<Object> values = list(invocation.getArgument(0, String.class));
          return values.isEmpty() ? null : values.remove(0);
        });
    when(listOperations.range(anyString(), anyLong(), anyLong()))
        .thenAnswer(invocation -> List.copyOf(list(invocation.getArgument(0, String.class))));
    when(listOperations.remove(anyString(), anyLong(), any()))
        .thenAnswer(invocation -> {
          List<Object> values = list(invocation.getArgument(0, String.class));
          Object value = invocation.getArgument(2);
          boolean removed = values.remove(value);
          return removed ? 1L : 0L;
        });

    doAnswer(invocation -> {
          Set<Object> values = set(invocation.getArgument(0, String.class));
          Object[] newValues = valuesArgument(invocation.getArgument(1));
          long added = 0;
          for (Object value : newValues) {
            if (values.add(value)) {
              added++;
            }
          }
          return added;
        })
        .when(setOperations).add(anyString(), org.mockito.ArgumentMatchers.<Object[]>any());
    doAnswer(invocation -> {
          Set<Object> values = set(invocation.getArgument(0, String.class));
          Object[] removeValues = valuesArgument(invocation.getArgument(1));
          long removed = 0;
          for (Object value : removeValues) {
            if (values.remove(value)) {
              removed++;
            }
          }
          return removed;
        })
        .when(setOperations).remove(anyString(), org.mockito.ArgumentMatchers.<Object[]>any());
    doAnswer(invocation -> set(invocation.getArgument(0, String.class))
        .contains(valuesArgument(invocation.getArgument(1))[0]))
        .when(setOperations).isMember(any(), any());

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

  private List<Object> list(String key) {
    return lists.computeIfAbsent(key, ignored -> new ArrayList<>());
  }

  private Set<Object> set(String key) {
    return sets.computeIfAbsent(key, ignored -> new HashSet<>());
  }

  private Object[] valuesArgument(Object argument) {
    return argument instanceof Object[] values ? values : new Object[]{argument};
  }
}
