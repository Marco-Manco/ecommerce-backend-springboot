package com.ecommerce.ecommerce.infrastructure.security;

import com.ecommerce.ecommerce.application.dto.AuthResponse;
import com.ecommerce.ecommerce.application.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public OAuth2SuccessHandler(AuthService authService, ObjectMapper objectMapper){
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    )throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String nombre = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub");

        AuthResponse authResponse = authService.procesarOAuth2Login(email, nombre, googleId);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), authResponse);
    }
}
