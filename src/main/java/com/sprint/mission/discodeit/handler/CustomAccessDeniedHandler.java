package com.sprint.mission.discodeit.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                "FORBIDDEN",
                "해당 리소스에 접근할 권한이 없습니다.",
                null,
                accessDeniedException.getClass().getSimpleName(),
                HttpServletResponse.SC_FORBIDDEN
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
