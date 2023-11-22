package com.api.service.impl;

import java.io.IOException;
import java.util.*;

import com.api.dto.response.ResponseErrorTemplate;
import com.api.exception.BaseException;
import com.api.utils.ConstantUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.dto.request.LoginRequest;
import com.api.dto.request.RegisterRequest;
import com.api.dto.response.AuthResponse;
import com.api.model.Role;
import com.api.model.User;
import com.api.repository.RoleRepository;
import com.api.repository.UserRepository;
import com.api.security.jwt.JwtService;
import com.api.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    //private final AuthenticationManager authenticationManager;

    @Override
    public ResponseErrorTemplate register(RegisterRequest request) throws RuntimeException {

        validateAccount(request);
        
        Set<Role> roles = findAllRoleById(request.getRoleID());

        User user = new User();
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode( request.getPassword() ));
        user.setPhoneNumber( request.getPhoneNumber() );
        user.setAttempt(0);
        user.setEnable(true);
        user.setAccountNonLocked(true);
        user.setLockTime(null);
        user.setRoles(roles);

        String genToken = jwtService.generateToken(user);
        String genRefreshToken = jwtService.generateRefreshToken(user);
        user = userRepository.save(user);

        AuthResponse response = AuthResponse.builder()
                .accessToken(genToken)
                .refreshToken(genRefreshToken)
                .build();

        return ResponseErrorTemplate.builder()
                .code(ConstantUtils.SC_OK)
                .message("OK")
                .data(response)
                .build();
    }

//    @Override
//    public AuthResponse authenticate(LoginRequest request)  {
//
//        authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//        );
//
//        Optional<User> user = userRepository.findByEmail(request.getEmail());
//
//        String genToken = jwtService.generateToken(user.get());
//        String genRefreshToken = jwtService.generateRefreshToken(user.get());
//
//        AuthResponse authResponse = new AuthResponse();
//        authResponse.setAccessToken(genToken);
//        authResponse.setRefreshToken(genRefreshToken);
//
//        return authResponse;
//    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwtRefreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ") ) {
            return;
        }

        jwtRefreshToken = authHeader.substring(7);

        try{
            userEmail = jwtService.extractUsername(jwtRefreshToken);
            if (userEmail != null) {
                
                User user = userRepository.findByEmail(userEmail).orElseThrow( () -> new UsernameNotFoundException("Email not found...") );
                
                if (jwtService.isTokenValid(jwtRefreshToken, user)) {
                    var accessToken = jwtService.generateToken(user);
                    AuthResponse auhtResponse = new AuthResponse();
                    auhtResponse.setAccessToken(accessToken);
                    auhtResponse.setRefreshToken(jwtRefreshToken);
                    new ObjectMapper().writeValue(response.getOutputStream(), auhtResponse);
                }
            }
        }catch(ExpiredJwtException ex){
            log.warn( "{} refresh token was expired.", ex.getMessage());
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT);
        }

    }

    @Override
    public void saveUserAttemptAuthentication(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()){
            if (user.get().isEnabled() && user.get().isAccountNonLocked()){
                if (user.get().getAttempt() < ConstantUtils.MAX_FAILED_ATTEMPTS - 1){
                    this.increaseFailedAttempts(user.get());
                }else {
                    this.lock(user.get());
                    throw new BaseException(ConstantUtils.SC_BD, "Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 24 hours.");
                }
            }else if(!user.get().getAccountNonLocked()) {
                if (this.unlockWhenTimeExpired(user.get())) {
                    throw new BaseException(ConstantUtils.SC_BD,"Your account has been unlocked. Please try to login again.");
                }
            }
        }
    }

    @Override
    public void updateAttempt(String email) {
        this.resetFailedAttempts(email);
    }
    private void increaseFailedAttempts(User user) {
        int attempt = user.getAttempt() + 1;
        userRepository.updateFailedAttempts(attempt, user.getEmail());
    }
    private void resetFailedAttempts(String email) {
        userRepository.updateFailedAttempts(0, email);
    }
    private void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }
    private boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();
        if (lockTimeInMillis + ConstantUtils.LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setAttempt(0);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    private void validateAccount(RegisterRequest request){
        // validate null data
        if(ObjectUtils.isEmpty(request)){
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Data must not Empty"); //
        }

        // validate duplicate email
        if (existsEmail(request.getEmail()) ) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Email has exist");
        }
        // validate role
        request.getRoleID().forEach(
                id -> {
                    Optional<Role> role = roleRepository.findById(id);
                    if (role.isEmpty()){
                        throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "role id %d not found".formatted(id));
                    }
                }
        );
    }
    private Boolean existsEmail(String email){
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return true;
        }
        return false;
    }
    private Set<Role> findAllRoleById(Set<Long> id){

        Set<Role> roles = new HashSet<>();

        if (id.isEmpty()) {
            Role role = roleRepository.findByName("USER");
            roles.add(role);
            return roles;
        }

        id.forEach( (roleId) -> {
            Role role = roleRepository.findById(roleId).get();
            roles.add(role);
        });    

        return roles;
    }

}
