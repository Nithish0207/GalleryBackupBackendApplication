package com.nithish.gallery.gallerybackup.controller;


import com.nithish.gallery.gallerybackup.config.JwtService;
import com.nithish.gallery.gallerybackup.dto.ApiResponse;
import com.nithish.gallery.gallerybackup.dto.LoginRequest;
import com.nithish.gallery.gallerybackup.dto.RegisterRequest;
import com.nithish.gallery.gallerybackup.model.User;
import com.nithish.gallery.gallerybackup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.nithish.gallery.gallerybackup.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword()
            );

            String token = jwtService.generateToken(
                    org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
                            .password(user.getPassword())
                            .roles(user.getRole().name())
                            .build()
            );

            return ResponseEntity.ok(ApiResponse.success("Registration successful", token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);

            User user = userService.findByEmail(request.getEmail());

            return ResponseEntity.ok(ApiResponse.success("Login successful", Map.of(
                    "token", token,
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole()
            )));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid email or password"));
        }
    }

    // Create admin user (for development only - remove in production)
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody RegisterRequest request) {
        try {
            User admin = userService.createAdmin(
                    request.getEmail(),
                    request.getPassword()
            );

            admin.setName(request.getName());
            userRepository.save(admin);

            return ResponseEntity.ok(ApiResponse.success(
                    "Admin created successfully",
                    Map.of("email", admin.getEmail(), "role", admin.getRole())
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}