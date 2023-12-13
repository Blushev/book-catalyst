package com.example.bookcatalog.service;


import com.example.bookcatalog.model.User;
import com.example.bookcatalog.repository.UserRepository;
import com.example.bookcatalog.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class MyUserDetailsService implements UserDetailsService {

@Autowired
    private UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Optional<User> user = repository.findByUsername(username);
        return user.map(MyUserDetails::new)
                .orElseThrow(()->new UsernameNotFoundException(username + " not found"));
    }

}