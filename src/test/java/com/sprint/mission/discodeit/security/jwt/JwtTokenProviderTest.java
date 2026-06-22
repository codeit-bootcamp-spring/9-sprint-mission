package com.sprint.mission.discodeit.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nimbusds.jose.JOSEException;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;
  private DiscodeitUserDetails userDetails;

  @BeforeEach
  void setUp() throws JOSEException {
    String testAccessSecret = "test-access-secret-key-for-jwt-token-generation-and-validation-must-be-long-enough";
    String testRefreshSecret = "test-refresh-secret-key-for-jwt-token-generation-and-validation-must-be-long-enough";
    int testAccessExpirationMs = 1800000; // 30 minutes
    int testRefreshExpirationMs = 604800000; // 7 days

    jwtTokenProvider = new JwtTokenProvider(testAccessSecret, testAccessExpirationMs,
        testRefreshSecret, testRefreshExpirationMs);

    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false,
        Role.USER
    );

    userDetails = new DiscodeitUserDetails(userDto, "encoded-password");
  }

  @Test
  @DisplayName("JWT 토큰 생성 테스트")
  void generateAccessToken_Success() throws JOSEException {
    // When
    String token = jwtTokenProvider.generateAccessToken(userDetails);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3); // JWT should have 3 parts: header.payload.signature
  }

  @Test
  @DisplayName("유효한 JWT 토큰 검증 테스트")
  void validateToken_ValidAccessToken_ReturnsTrue() throws JOSEException {
    // Given
    String token = jwtTokenProvider.generateAccessToken(userDetails);

    // When
    boolean isValid = jwtTokenProvider.validateAccessToken(token);

    // Then
    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("잘못된 JWT 토큰 검증 테스트")
  void validateToken_InvalidAccessToken_ReturnsFalse() {
    // Given
    String invalidToken = "invalid.jwt.token";

    // When
    boolean isValid = jwtTokenProvider.validateAccessToken(invalidToken);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("null 토큰 검증 테스트")
  void validateToken_NullAccessToken_ReturnsFalse() {
    // When
    boolean isValid = jwtTokenProvider.validateAccessToken(null);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("빈 토큰 검증 테스트")
  void validateToken_EmptyAccessToken_ReturnsFalse() {
    // When
    boolean isValid = jwtTokenProvider.validateAccessToken("");

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("JWT 토큰에서 사용자명 추출 테스트")
  void getUsernameFromToken_ValidToken_ReturnsUsername() throws JOSEException {
    // Given
    String token = jwtTokenProvider.generateAccessToken(userDetails);

    // When
    String username = jwtTokenProvider.getUsernameFromToken(token);

    // Then
    assertThat(username).isEqualTo("testuser");
  }

  @Test
  @DisplayName("잘못된 토큰에서 사용자명 추출 테스트 - 예외 발생")
  void getUsernameFromToken_InvalidToken_ThrowsException() {
    // Given
    String invalidToken = "invalid.jwt.token";

    // When & Then
    assertThatThrownBy(() -> jwtTokenProvider.getUsernameFromToken(invalidToken))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid JWT token");
  }

  @Test
  @DisplayName("JWT 토큰에서 토큰 ID 추출 테스트")
  void getTokenId_ValidToken_ReturnsTokenId() throws JOSEException {
    // Given
    String token = jwtTokenProvider.generateAccessToken(userDetails);

    // When
    String tokenId = jwtTokenProvider.getTokenId(token);

    // Then
    assertThat(tokenId).isNotNull();
    assertThat(tokenId).isNotEmpty();
    // UUID format check
    assertThat(UUID.fromString(tokenId)).isNotNull();
  }

  @Test
  @DisplayName("잘못된 토큰에서 토큰 ID 추출 테스트 - 예외 발생")
  void getTokenId_InvalidToken_ThrowsException() {
    // Given
    String invalidToken = "invalid.jwt.token";

    // When & Then
    assertThatThrownBy(() -> jwtTokenProvider.getTokenId(invalidToken))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid JWT token");
  }

  @Test
  @DisplayName("만료된 토큰 검증 테스트")
  void validateToken_ExpiredAccessToken_ReturnsFalse() throws JOSEException {
    // Given - Create provider with very short expiration (1ms)
    JwtTokenProvider shortExpirationProvider = new JwtTokenProvider(
        "test-access-secret-key-for-jwt-token-generation-and-validation-must-be-long-enough",
        1,
        "test-refresh-secret-key-for-jwt-token-generation-and-validation-must-be-long-enough",
        604800000
    );

    String token = shortExpirationProvider.generateAccessToken(userDetails);

    // Wait for token to expire
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // When
    boolean isValid = shortExpirationProvider.validateAccessToken(token);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("다른 사용자의 토큰 생성 및 검증 테스트")
  void generateAccessToken_DifferentUser_HasDifferentClaims() throws JOSEException {
    // Given
    UUID anotherUserId = UUID.randomUUID();
    UserDto anotherUserDto = new UserDto(
        anotherUserId,
        "anotheruser",
        "another@example.com",
        null,
        true,
        Role.ADMIN
    );
    DiscodeitUserDetails anotherUserDetails = new DiscodeitUserDetails(anotherUserDto,
        "another-password");

    // When
    String token1 = jwtTokenProvider.generateAccessToken(userDetails);
    String token2 = jwtTokenProvider.generateAccessToken(anotherUserDetails);

    // Then
    assertThat(token1).isNotEqualTo(token2);
    assertThat(jwtTokenProvider.getUsernameFromToken(token1)).isEqualTo("testuser");
    assertThat(jwtTokenProvider.getUsernameFromToken(token2)).isEqualTo("anotheruser");
    assertThat(jwtTokenProvider.getTokenId(token1)).isNotEqualTo(
        jwtTokenProvider.getTokenId(token2));
  }
}