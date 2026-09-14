package com.tutormarketplace.controller;

import com.tutormarketplace.dto.LoginRequest;
import com.tutormarketplace.dto.LoginResponse;
import com.tutormarketplace.dto.RegisterRequest;
import com.tutormarketplace.dto.UserDTO;
import com.tutormarketplace.model.User;
import com.tutormarketplace.security.JwtTokenProvider;
import com.tutormarketplace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and registration")
public class AuthController {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new student account")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFullName()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.toDTO(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and receive JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Find user by username or email
        User user = userService.findByUsername(request.getUsernameOrEmail())
            .or(() -> userService.findByEmail(request.getUsernameOrEmail()))
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);
        long expiresIn = jwtTokenProvider.getExpirationTime();
        
        LoginResponse response = LoginResponse.builder()
            .token(token)
            .user(userService.toDTO(user))
            .expiresIn(expiresIn)
            .build();
        
        return ResponseEntity.ok(response);
    }
}
