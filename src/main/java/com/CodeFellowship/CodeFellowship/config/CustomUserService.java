package com.CodeFellowship.CodeFellowship.config;

import com.CodeFellowship.CodeFellowship.models.AppUser;
import com.CodeFellowship.CodeFellowship.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserService implements UserDetailsService {
    @Autowired
    AppUserRepository appUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) {
            System.out.println("User not found " + username);
            throw new UsernameNotFoundException("user" + username + " was not found in the db");
        }
        System.out.println("Found User: " + appUser.getUsername());
        return  appUser;
    }
}
