package com.api.service;

import java.io.IOException;

import com.api.dto.request.LoginRequest;
import com.api.dto.request.RegisterRequest;
import com.api.dto.response.AuthResponse;


import com.api.dto.response.ResponseErrorTemplate;
import com.api.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    ResponseErrorTemplate register(RegisterRequest request) throws RuntimeException;
    //AuthResponse authenticate(LoginRequest request);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
    void saveUserAttemptAuthentication(String email);
    void updateAttempt(String email);
}
