package com.example.auth.web;

import com.example.auth.security.JwtService;
import com.example.auth.user.User;
import com.example.auth.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public record RegisterRequest(@NotBlank String username, @Email String email, @NotBlank String password) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username()) || userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username or email already exists"));
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        String token = jwtService.generateToken(user.getUserId(), user.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of("token", jwtService.generateToken(u.getUserId(), u.getUsername()))))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("message", "Invalid credentials")));
    }
}


