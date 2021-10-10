package com.rutkoski.auth.services;

import com.rutkoski.auth.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserService userService;

    public String encodePassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public boolean verifyPasswords(String password, String encPass){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, encPass);
    }

    public boolean validatePersist(User user) {
        if (user == null) {
            return false;
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return false;
        }
        if(user.getPassword() == null || user.getPassword().isEmpty()){
            return false;
        }
        return true;
    }

    public boolean alreadyExists(String username) {
        User user = userService.findByUsername(username);
        return user != null;
    }

    public boolean validateAuth(User entity){
        if(!this.validatePersist(entity)){
            return false;
        }
        User dbUser = this.userService.findByUsername(entity.getUsername());
        if(dbUser == null){
            return false;
        }

        return verifyPasswords(entity.getPassword(), dbUser.getPassword());
    }

    public User persist(User entity){
        entity.setPassword(this.encodePassword(entity.getPassword()));
        return this.userService.insert(entity);
    }
}
