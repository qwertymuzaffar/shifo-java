package com.shifo.shifo_java.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Check if the request has reached a handler (if not, it's a 404)
        Object handlerAttribute = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");
        boolean isNotFound = handlerAttribute == null;

        if (isNotFound) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_NOT_FOUND);
            body.put("error", "Not Found");
            body.put("message", "The requested resource was not found");
            body.put("path", request.getServletPath());

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", authException.getMessage());
            body.put("path", request.getServletPath());

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
        }
    }
}
