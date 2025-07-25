package com.glancy.backend.config.auth;

import com.glancy.backend.entity.User;
import com.glancy.backend.exception.InvalidRequestException;
import com.glancy.backend.exception.UnauthorizedException;
import com.glancy.backend.exception.BusinessException;
import com.glancy.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Resolves parameters annotated with {@link AuthenticatedUser} by validating
 * the user token from request headers.
 */
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserService userService;

    public AuthenticatedUserArgumentResolver(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(AuthenticatedUser.class)) {
            return false;
        }
        Class<?> type = parameter.getParameterType();
        return User.class.isAssignableFrom(type) || Long.class.equals(type);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = request.getHeader("X-USER-TOKEN");
        if (token == null) {
            throw new UnauthorizedException("Missing X-USER-TOKEN header");
        }

        Map<String, String> pathVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String userIdStr = null;
        if (pathVars != null) {
            userIdStr = pathVars.get("userId");
            if (userIdStr == null) {
                userIdStr = pathVars.get("id");
            }
        }
        if (userIdStr == null) {
            userIdStr = request.getParameter("userId");
        }
        if (userIdStr == null) {
            throw new InvalidRequestException("Missing userId");
        }
        Long userId = Long.valueOf(userIdStr);
        try {
            userService.validateToken(userId, token);
        } catch (BusinessException ex) {
            throw new UnauthorizedException("Invalid token");
        }

        if (Long.class.equals(parameter.getParameterType())) {
            return userId;
        }
        return userService.getUserRaw(userId);
    }
}
