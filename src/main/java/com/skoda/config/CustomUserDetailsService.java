package com.skoda.config;

import com.skoda.dao.User;
import com.skoda.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = authService.getUserByUserName(username);

        log.info("[loadUserByUsername] {}", user);

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                new ArrayList<>()
        );
    }
}
