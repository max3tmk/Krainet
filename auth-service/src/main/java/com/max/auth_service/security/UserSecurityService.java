package com.max.auth_service.security;

import com.max.auth_service.dto.RegisterRequest;
import com.max.auth_service.entity.User;
import com.max.auth_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User save(User user, PasswordEncoder passwordEncoder) {
        if (user.getPassword() == null || !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (!StringUtils.hasText(user.getRole())) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User register(RegisterRequest request, PasswordEncoder encoder) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Username and password must not be blank");
        }

        String normalizedRole = normalizeRole(request.getRole());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(normalizedRole);

        return userRepository.save(user);
    }

    private String normalizeRole(String input) {
        if (!StringUtils.hasText(input)) {
            return "ROLE_USER";
        }
        return input.startsWith("ROLE_") ? input : "ROLE_" + input.toUpperCase();
    }
}
