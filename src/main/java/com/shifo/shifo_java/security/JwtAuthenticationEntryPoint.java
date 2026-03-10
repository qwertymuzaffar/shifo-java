package com.shifo.shifo_java.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final org.springframework.util.AntPathMatcher PATH_MATCHER = new org.springframework.util.AntPathMatcher();
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        int status = isRequestMappingMissing(request)
                ? HttpServletResponse.SC_NOT_FOUND
                : HttpServletResponse.SC_UNAUTHORIZED;
        response.setStatus(status);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", status == HttpServletResponse.SC_NOT_FOUND ? "Not Found" : "Unauthorized");
        body.put("message", status == HttpServletResponse.SC_NOT_FOUND
                ? "The requested resource was not found"
                : authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

    private boolean isRequestMappingMissing(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        String requestMethod = request.getMethod();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo info = entry.getKey();
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();

            if (!methods.isEmpty() && methods.stream().noneMatch(m -> m.name().equals(requestMethod))) {
                continue;
            }

            if (matchesPathPattern(info, path)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesPathPattern(RequestMappingInfo info, String path) {
        if (info.getPathPatternsCondition() != null) {
            return info.getPathPatternsCondition()
                    .getPatternValues()
                    .stream()
                    .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
        }

        if (info.getPatternsCondition() != null) {
            return info.getPatternsCondition()
                    .getPatterns()
                    .stream()
                    .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
        }

        return false;
    }
}
