package com.api.controller;

import java.io.IOException;

import com.api.dto.response.ResponseErrorTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.dto.request.LoginRequest;
import com.api.dto.request.RegisterRequest;
import com.api.dto.response.AuthResponse;
import com.api.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) throws RuntimeException {
        ResponseErrorTemplate register = userService.register(registerRequest);
        return ResponseEntity.ok(register);
    }

//    @PostMapping("authenticate")
//    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request){
//        AuthResponse response = userService.authenticate(request);
//        return ResponseEntity.ok().body(response);
//    }

    @GetMapping("refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
        userService.refreshToken(request, response);
    }

}
