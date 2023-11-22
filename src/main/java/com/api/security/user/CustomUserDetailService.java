package com.api.security.user;

import java.util.Optional;


import com.api.exception.BaseException;
import com.api.utils.ConstantUtils;
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
        return getUserDetails(username);
    }

    private UserDetails getUserDetails(String username){
        Optional<User> user = userRepository.findByEmail(username);

        //validate user
        this.validateUser(user);

        return user.get();
    }

    private void validateUser(Optional<User> user){
        if( user.isEmpty() ){
            log.warn("{}", user.get().getEmail());
            throw new BaseException(ConstantUtils.SC_NF, "User Email Is Not Found!");
        }
    }
    
}
