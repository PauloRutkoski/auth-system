package com.rutkoski.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rutkoski.login.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
