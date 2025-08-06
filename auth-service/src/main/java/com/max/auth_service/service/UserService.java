package com.max.auth_service.service;

import com.max.auth_service.entity.User;
import com.max.auth_service.enums.UserEventType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id, UserDetails userDetails);
    boolean deleteUserById(Long id, UserDetails userDetails);
    User updateUserById(Long id, User updatedUser);
    List<User> getAllUsers();
    Optional<User> findByUsername(String username);
    User saveUser(User user);
    void notifyUserCreatedOrUpdated(User user, UserEventType eventType, String rawPassword);
}
