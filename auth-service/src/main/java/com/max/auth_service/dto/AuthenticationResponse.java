package com.max.auth_service.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {
    private final String jwt;
}
