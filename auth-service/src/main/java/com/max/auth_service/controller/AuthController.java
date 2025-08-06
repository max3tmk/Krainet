package com.max.auth_service.controller;

import com.max.auth_service.client.NotificationClient;
import com.max.auth_service.dto.AuthenticationRequest;
import com.max.auth_service.dto.AuthenticationResponse;
import com.max.auth_service.dto.RegisterRequest;
import com.max.auth_service.dto.UserEventNotificationRequest;
import com.max.auth_service.entity.User;
import com.max.auth_service.enums.UserEventType;
import com.max.auth_service.repository.UserRepository;
import com.max.auth_service.security.UserSecurityService;
import com.max.auth_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserSecurityService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final NotificationClient notificationClient;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest request) {
        log.info("Operation: POST /auth/login — Attempting login for user '{}'", request.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            log.info("Login successful for user '{}'", request.getUsername());
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user '{}': Incorrect username or password", request.getUsername());
            return ResponseEntity.status(401).body("Incorrect username or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        log.info("JWT token generated for user '{}'", request.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        log.info("Operation: POST /auth/register — Registering user '{}'", request.getUsername());

        if (userDetailsService.userExists(request.getUsername())) {
            log.warn("Registration failed — username '{}' already exists", request.getUsername());
            return ResponseEntity.badRequest().body("Username already exists");
        }

        userDetailsService.register(request, passwordEncoder);
        log.info("User '{}' registered successfully", request.getUsername());

        User registeredUser = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (registeredUser != null && "ROLE_USER".equalsIgnoreCase(registeredUser.getRole())) {
            try {
                UserEventNotificationRequest notificationRequest = new UserEventNotificationRequest(
                        registeredUser,
                        UserEventType.CREATED,
                        request.getPassword()
                );
                notificationClient.notifyUserEvent(notificationRequest);
                log.info("Notification sent for newly registered user '{}'", registeredUser.getUsername());
            } catch (Exception e) {
                log.error("Failed to send notification for registered user '{}': {}", registeredUser.getUsername(), e.getMessage());
            }
        }

        return ResponseEntity.ok("User registered successfully");
    }
}
