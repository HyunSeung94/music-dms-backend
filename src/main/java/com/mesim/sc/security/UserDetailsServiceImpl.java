package com.mesim.sc.security;

import com.mesim.sc.repository.rdb.admin.group.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userRepository.findById(username).map(user -> {
                org.springframework.security.core.userdetails.User.UserBuilder builder = null;
                builder = org.springframework.security.core.userdetails.User.withUsername(username);
                builder.password(user.getPassword());
                builder.roles(String.valueOf(user.getAuthorityId()));
                return builder.build();
            }).get();
        } catch (NoSuchElementException e) {
            log.error("Username(id) not found. username(id) : {}", username);
            throw new UsernameNotFoundException("User not found.");
        }
    }
}
