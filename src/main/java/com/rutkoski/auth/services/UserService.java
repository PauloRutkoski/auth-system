package com.rutkoski.auth.services;

import com.rutkoski.auth.domain.User;
import com.rutkoski.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User insert(User user) {
        return repository.save(user);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }



}
