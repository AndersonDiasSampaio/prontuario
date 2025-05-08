package org.example.prontuario.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final CustomAuthenticationSuccessHandler successHandler;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, 
                                    CustomAuthenticationSuccessHandler successHandler) {
        this.authenticationManager = authenticationManager;
        this.successHandler = successHandler;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login2", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, 
                                             HttpServletResponse response) throws AuthenticationException {
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(username, password);
        
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, 
                                          HttpServletResponse response, 
                                          FilterChain chain,
                                          Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, 
                                            HttpServletResponse response,
                                            AuthenticationException failed) throws IOException, ServletException {
        response.sendRedirect("/login2?error");
    }
} 