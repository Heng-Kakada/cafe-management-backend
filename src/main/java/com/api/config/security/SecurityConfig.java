package com.api.config.security;

import com.api.config.security.filter.CustomAuthenticationProvider;
import com.api.config.security.filter.JwtAuthenticationAttemptFilter;
import com.api.config.security.filter.JwtAuthenticationFilter;
import com.api.config.security.jwt.JwtService;
import com.api.config.security.user.CustomUserDetailService;
import com.api.handler.CustomAccessDeniedHandler;
import com.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomUserDetailService customUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public void userAuthenticationGlobalConfig(AuthenticationManagerBuilder authenticationManagerBuilder){
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder);
        AuthenticationManager manager = builder.build();

        http    .cors( cors -> cors.disable() )
                .csrf( csrf -> csrf.disable() )
                .formLogin( form -> form.disable() )
                .authorizeHttpRequests( auth -> auth
                                            .requestMatchers("/api/auth/**").permitAll()
                                            .requestMatchers("/api/dashboard/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                                            .requestMatchers("/api/categories/**").hasAnyRole("MANAGER","ADMIN")
                                            .requestMatchers("/api/products/**").hasAnyRole("MANAGER","ADMIN")
                                            .requestMatchers("/api/orders/**").hasAnyRole("MANAGER","ADMIN")
                                    )
                .sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(manager)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .addFilterBefore(new JwtAuthenticationAttemptFilter(objectMapper,jwtService,userService,manager), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(jwtService, customUserDetailService), UsernamePasswordAuthenticationFilter.class )
        ;
        return http.build();
    }
}
