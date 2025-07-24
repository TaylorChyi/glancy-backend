package com.glancy.backend.config;

import com.glancy.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * Interceptor that validates the {@code X-USER-TOKEN} header for requests
 * targeting user-specific endpoints.
 */
@Component
public class TokenAuthenticationInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public TokenAuthenticationInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("X-USER-TOKEN");
        if (token == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing X-USER-TOKEN header");
            return false;
        }

        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String userIdStr = null;
        if (pathVariables != null) {
            userIdStr = pathVariables.get("userId");
        }
        if (userIdStr == null) {
            userIdStr = request.getParameter("userId");
        }
        if (userIdStr == null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing userId");
            return false;
        }
        try {
            Long userId = Long.valueOf(userIdStr);
            userService.validateToken(userId, token);
            return true;
        } catch (Exception ex) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
            return false;
        }
    }
}
