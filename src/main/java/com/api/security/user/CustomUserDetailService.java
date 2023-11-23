package com.api.security.user;

import java.util.Objects;
import java.util.Optional;


import com.api.exception.BaseException;
import com.api.utils.ConstantUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.api.model.User;
import com.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        this.validateUser(user);
        return user;
    }
    private void validateUser(User user){

        if(Objects.isNull(user)){
            throw new BaseException(ConstantUtils.SC_UA, "User Not Found!");
        }

    }
    
}
