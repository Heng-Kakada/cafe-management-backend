package com.api.security.filter;

import com.api.dto.request.LoginRequest;
import com.api.dto.response.AuthResponse;
import com.api.exception.BaseException;
import com.api.security.jwt.JwtService;
import com.api.security.user.CustomUserDetailService;
import com.api.service.UserService;
import com.api.utils.ConstantUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationAttemptFilter extends AbstractAuthenticationProcessingFilter {
    private ObjectMapper objectMapper;
    private JwtService jwtService;
    private UserService userService;
    public JwtAuthenticationAttemptFilter(ObjectMapper objectMapper,
                                          JwtService jwtService,
                                          UserService userService,
                                          AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/api/auth/authenticate", "POST"), authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        log.info("Start attempt to authentication");
        LoginRequest loginRequest  = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

        //userService.increaseFailedAttempts(loginRequest.getEmail());
        log.info("End attempt to authentication");

        // TODO fix duplicate response when BaseException message : Your account has been locked due to 3 failed attempts."
        //                            + " It will be unlocked after 24 hours. and  message : Your account has been unlocked. Please try to login again.  raise


        try {
            userService.saveUserAttemptAuthentication(loginRequest.getEmail());
        }catch (BaseException e){
            var msgJson = objectMapper.writeValueAsString(e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(msgJson);
        }

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword(),
                        Collections.emptyList())
                );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        userService.updateAttempt(userDetails.getUsername());

        AuthResponse authenticationResponse = new AuthResponse(
                accessToken,
                refreshToken
        );

        var jsonUser = objectMapper.writeValueAsString(authenticationResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonUser);
        log.info("Successful Authentication {}", authenticationResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        BaseException e = new BaseException();
        e.setCode(ConstantUtils.SC_UA);
        e.setMessage(failed.getLocalizedMessage());

        var msgJson = objectMapper.writeValueAsString(e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(msgJson);

        log.info("Unsuccessful Authentication {}", failed.getLocalizedMessage());
    }


}
