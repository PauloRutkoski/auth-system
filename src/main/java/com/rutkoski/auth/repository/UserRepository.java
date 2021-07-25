package com.rutkoski.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rutkoski.auth.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
}
