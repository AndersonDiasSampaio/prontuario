package org.example.prontuario.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String targetUrl = "/pacientes";
        
        log.info("User authenticated: {}", authentication.getName());
        log.info("User authorities: {}", authentication.getAuthorities());
        
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            log.info("Checking authority: {}", auth.getAuthority());
            if (auth.getAuthority().equals("ROLE_ADMIN")) {
                targetUrl = "/dashboard2";
                break;
            }
        }
        
        log.info("Redirecting to: {}", targetUrl);
        response.sendRedirect(targetUrl);
    }
} 