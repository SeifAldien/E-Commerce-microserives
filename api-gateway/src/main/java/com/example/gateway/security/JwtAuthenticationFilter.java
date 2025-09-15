package com.example.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final SecretKey signingKey;
    private final List<String> whitelistPrefixes;

    public JwtAuthenticationFilter(
            @Value("${security.jwt.secret:dev-secret-change}") String secret,
            @Value("${security.jwt.whitelist:/auth/}") List<String> whitelistPrefixes
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.whitelistPrefixes = whitelistPrefixes;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        for (String prefix : whitelistPrefixes) {
            if (path.startsWith(prefix)) {
                return chain.filter(exchange);
            }
        }

        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeaders.get(0).substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            ServerHttpRequest mutated = request.mutate()
                    .header("X-User-Id", String.valueOf(claims.get("userId")))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (Exception ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}


