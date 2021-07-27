package com.rutkoski.auth.controllers;

import com.rutkoski.auth.domain.User;
import com.rutkoski.auth.services.AuthService;
import com.rutkoski.auth.services.UserService;
import com.rutkoski.auth.utils.JwtUtils;
import io.jsonwebtoken.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/hello", produces = "application/json")
public class HelloController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<String> hello(@RequestHeader Map<String, String> header){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = formatter.format(Date.valueOf(LocalDate.now()));
        StringBuilder builder = new StringBuilder();
        builder.append("Hello ").append(SecurityContextHolder.getContext().getAuthentication().getName());
        builder.append(" today is ").append(date);
        return ResponseEntity.ok(builder.toString());
    }
}
