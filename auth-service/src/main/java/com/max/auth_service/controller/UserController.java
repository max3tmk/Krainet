package com.max.auth_service.controller;

import com.max.auth_service.client.NotificationClient;
import com.max.auth_service.entity.User;
import com.max.auth_service.repository.UserRepository;
import com.max.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("GET /users - Fetching all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("GET /users/me - Unauthorized access attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            log.warn("GET /users/me - User not found: {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        log.info("GET /users/me - Returning info for user: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("GET /users/{} - Unauthorized access attempt", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            var userOpt = userService.getUserById(id, userDetails);
            if (userOpt.isEmpty()) {
                log.warn("GET /users/{} - User not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            log.info("GET /users/{} - Returning user info", id);
            return ResponseEntity.ok(userOpt.get());
        } catch (Exception e) {
            log.error("GET /users/{} - Access denied or error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id,
            @RequestBody User updatedUser,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            log.warn("PUT /users/{} - Unauthorized access attempt", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User caller = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (caller == null) {
            log.warn("PUT /users/{} - Caller not found", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        boolean isAdmin = caller.getRole().equalsIgnoreCase("ROLE_ADMIN");
        if (!isAdmin && !caller.getId().equals(id)) {
            log.warn("PUT /users/{} - Forbidden: caller {} tried to update user {}", id, caller.getUsername(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        User savedUser = userService.updateUserById(id, updatedUser);
        if (savedUser == null) {
            log.warn("PUT /users/{} - User not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        log.info("PUT /users/{} - User updated by {}", id, caller.getUsername());
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("DELETE /users/{} - Unauthorized access attempt", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        User caller = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (caller == null) {
            log.warn("DELETE /users/{} - Caller not found", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        boolean isAdmin = caller.getRole().equalsIgnoreCase("ROLE_ADMIN");
        if (!isAdmin && !caller.getId().equals(id)) {
            log.warn("DELETE /users/{} - Forbidden: caller {} tried to delete user {}", id, caller.getUsername(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }

        User userToDelete = userService.getUserById(id, userDetails).orElse(null);
        if (userToDelete == null) {
            log.warn("DELETE /users/{} - User not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        userService.deleteUserById(id, userDetails);
        log.info("DELETE /users/{} - User deleted by {}", id, caller.getUsername());
        return ResponseEntity.ok("User deleted");
    }
}
