package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.JwtInformation;
import com.sprint.mission.discodeit.repository.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.RefreshTokenStore;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final RefreshTokenStore refreshTokenStore;
    private final JwtRegistry jwtRegistry;
    private final CacheManager cacheManager;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
        UserDto userDto =  userDetails.getUserDto();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

        String refreshToken = jwtTokenProvider.generateRefreshToken();

        JwtInformation jwtInformation = new JwtInformation(userDto, accessToken, refreshToken);
        jwtRegistry.registerJwtInformation(jwtInformation);

        refreshTokenStore.save(refreshToken, userDetails.getUsername());
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/api/auth");
        refreshCookie.setMaxAge(7*24*60*60);
        response.addCookie(refreshCookie);

        JwtDto responseBody = JwtDto.builder()
                .accessToken(accessToken)
                .userDto(userDto)
                .build();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), responseBody);
        log.info("JWT login success: {}",userDetails.getUsername());

        Cache userCache = cacheManager.getCache("users");
        if (userCache != null) {
            userCache.clear();
            log.debug("로그인 성공: 'users' 캐시 초기화 완료");
        }
    }
}
