package com.rutkoski.auth.controllers;

import com.rutkoski.auth.domain.User;
import com.rutkoski.auth.services.AuthService;
import com.rutkoski.auth.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = "application/json")
public class AuthController {
    @Autowired
    private AuthService service;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody User entity){
        boolean auth = service.validateAuth(entity);
        if(!auth){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token",jwtUtils.generateToken(entity.getUsername(), 0));
        map.put("refresh_token",jwtUtils.generateToken(entity.getUsername(), 1));
        return ResponseEntity.ok(map);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Long> register(@RequestBody User entity){
        if(!service.validatePersist(entity)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(service.alreadyExists(entity.getUsername())){
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
        }
        entity = service.persist(entity);
        return ResponseEntity.ok(entity.getId());
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body){
        String token = body.get("refresh_token");
        if(token == null){
            throw new MalformedJwtException("Token 'refresh_token' not found in request");
        }
        String username = jwtUtils.getUsernameFromToken(token);

        Map<String, Object> map = new HashMap<>();
        map.put("token",jwtUtils.generateToken(username, 0));
        map.put("refresh_token",jwtUtils.generateToken(username, 1));
        return ResponseEntity.ok(map);
    }
}
