package com.api.config.security.filter;

import com.api.exception.BaseException;
import com.api.model.Role;
import com.api.model.User;
import com.api.repository.UserRepository;
import com.api.utils.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;


@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Start actual authentication");
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        User user;
        try {
            user = userRepository.findByEmail(email);
        }catch (Exception e){
            throw new BaseException(ConstantUtils.SC_UA, "User not found!");
        }
        log.info("End actual authentication");
        return new UsernamePasswordAuthenticationToken(email, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
