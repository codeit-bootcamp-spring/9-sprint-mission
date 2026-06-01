package com.sprint.mission.discodeit.dto.auth;

/**
 * 로그인 성공 시 응답 Body에 담기는 DTO
 * - accessToken: API 요청마다 Authorization 헤더에 담아 보내는 토큰
 */
public record JwtDto(
    String accessToken
) {

}
