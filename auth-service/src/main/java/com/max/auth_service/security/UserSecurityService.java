package com.max.auth_service.service;

import com.max.auth_service.dto.RegisterRequest;
import com.max.auth_service.entity.User;
import com.max.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    // Загрузка пользователя по username для Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Проверка, существует ли пользователь с таким username
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Сохранение нового пользователя (например, при регистрации)
    public User save(User user, PasswordEncoder passwordEncoder) {
        // Хешируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Если роль не указана — ставим ROLE_USER по умолчанию
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }

    // Регистрация пользователя из DTO запроса
    public void register(RegisterRequest request, PasswordEncoder encoder) {
        String normalizedRole = normalizeRole(request.getRole());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(normalizedRole);

        userRepository.save(user);
    }

    // Нормализация роли: если не начинается с "ROLE_", добавляем префикс и приводим к верхнему регистру
    private String normalizeRole(String input) {
        if (input == null || input.isBlank()) {
            return "ROLE_USER";
        }
        return input.startsWith("ROLE_") ? input : "ROLE_" + input.toUpperCase();
    }
}
