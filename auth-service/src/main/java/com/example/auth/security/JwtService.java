package com.example.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(Long userId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(Map.of("userId", userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}


