package com.rutkoski.auth.utils;

import com.rutkoski.auth.domain.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils implements Serializable {
    @Value("${jwt.exp_token}")
    private Long JWT_TOKEN_VALIDITY;
    @Value("${jwt.exp_ref_token}")
    private Long JWT_REFRESH_VALIDITY;
    @Value("${jwt.secret}")
    private String secret;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private JwsHeader getHeaderFromToken(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getHeader();
    }

    private Boolean isTokenExpired(String token, int type) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date(type == 0 ? this.JWT_TOKEN_VALIDITY : this.JWT_REFRESH_VALIDITY));
    }

    public String generateToken(String username, int type) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, username, type);
    }

    /**
     * @type 0 - AUTH_TOKEN
     * @type 1 - REFRESH_TOKEN
     */
    private String generateToken(Map<String, Object> claims, String subject, int type) {
        Long validity = this.JWT_TOKEN_VALIDITY;
        if (type == 1) {
            validity = this.JWT_REFRESH_VALIDITY;
        }
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Claims validateToken(String token, int type) {
        if(token == null || token.isEmpty()){
            throw new MalformedJwtException("Not valid token structure");
        }

        JwsHeader header = getHeaderFromToken(token);
        Claims claims = getAllClaimsFromToken(token);

        if(getUsernameFromToken(token) == null || getUsernameFromToken(token).isEmpty()){
            throw new MalformedJwtException("Not a valid token structure");
        }
        if(isTokenExpired(token, type)){
            throw new ExpiredJwtException(header, claims, "Session Expired");
        }
        return claims;
    }

}
