package com.max.auth_service.service;

import com.max.auth_service.client.NotificationClient;
import com.max.auth_service.dto.UserEventNotificationRequest;
import com.max.auth_service.entity.User;
import com.max.auth_service.enums.UserEventType;
import com.max.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NotificationClient notificationClient;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getUserById(Long id, UserDetails userDetails) {
        Optional<User> targetUser = userRepository.findById(id);
        if (targetUser.isEmpty()) {
            log.warn("User with id={} not found", id);
            return Optional.empty();
        }

        User currentUser = getCurrentUser(userDetails);

        if (hasAccessToUser(currentUser, id)) {
            log.info("Access granted for user '{}' to user with id={}", currentUser.getUsername(), id);
            return targetUser;
        }

        log.warn("Access denied for user '{}' to user with id={}", currentUser.getUsername(), id);
        throw new AccessDeniedException("You are not allowed to access this user");
    }

    @Override
    public boolean deleteUserById(Long id, UserDetails userDetails) {
        Optional<User> targetUser = userRepository.findById(id);
        if (targetUser.isEmpty()) {
            log.warn("User with id={} not found for deletion", id);
            return false;
        }

        User currentUser = getCurrentUser(userDetails);

        if (!hasAccessToUser(currentUser, id)) {
            log.warn("Access denied for user '{}' to delete user with id={}", currentUser.getUsername(), id);
            throw new AccessDeniedException("You are not allowed to delete this user");
        }

        userRepository.deleteById(id);

        if ("ROLE_USER".equalsIgnoreCase(targetUser.get().getRole())) {
            notifyUserCreatedOrUpdated(targetUser.get(), UserEventType.DELETED, "[не указано]");
        }

        log.info("User '{}' deleted user with id={}", currentUser.getUsername(), id);

        return true;
    }

    @Override
    public User updateUserById(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());

            String fullRole = mapToFullRole(updatedUser.getRole());
            user.setRole(fullRole);

            String rawPassword = updatedUser.getPassword();
            if (rawPassword != null && !rawPassword.isBlank()) {
                user.setPassword(passwordEncoder.encode(rawPassword));
            }

            User savedUser = userRepository.save(user);

            if ("ROLE_USER".equalsIgnoreCase(savedUser.getRole())) {
                notifyUserCreatedOrUpdated(savedUser, UserEventType.UPDATED,
                        rawPassword != null && !rawPassword.isBlank() ? rawPassword : "[не указано]");
            }

            return savedUser;
        }).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Fetched all users, count={}", users.size());
        return users;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User saveUser(User user) {
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(mapToFullRole(user.getRole()));

        User savedUser = userRepository.save(user);
        log.info("Saved user: username={}, id={}", savedUser.getUsername(), savedUser.getId());

        if ("ROLE_USER".equalsIgnoreCase(savedUser.getRole())) {
            notifyUserCreatedOrUpdated(savedUser, UserEventType.CREATED, rawPassword);
        }

        return savedUser;
    }

    @Override
    public void notifyUserCreatedOrUpdated(User user, UserEventType eventType, String rawPassword) {
        try {
            log.info("Preparing to send notification: user='{}', event='{}'", user.getUsername(), eventType);
            UserEventNotificationRequest request = new UserEventNotificationRequest(user, eventType, rawPassword);
            notificationClient.notifyUserEvent(request);
            log.info("Notification sent for user '{}' event '{}'", user.getUsername(), eventType);
        } catch (Exception e) {
            log.error("Failed to send notification for user '{}' event '{}': {}", user.getUsername(), eventType, e.getMessage());
        }
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.warn("Current user '{}' not found in DB", userDetails.getUsername());
                    return new UsernameNotFoundException("Current user not found");
                });
    }

    private boolean hasAccessToUser(User currentUser, Long targetUserId) {
        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("ROLE_ADMIN") || currentUser.getRole().equalsIgnoreCase("ADMIN");
        boolean isSelf = currentUser.getId().equals(targetUserId);
        return isAdmin || isSelf;
    }

    private String mapToFullRole(String role) {
        if (role == null) return null;
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
