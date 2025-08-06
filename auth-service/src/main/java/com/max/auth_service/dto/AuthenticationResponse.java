package com.max.auth_service.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private final String jwt;
}
