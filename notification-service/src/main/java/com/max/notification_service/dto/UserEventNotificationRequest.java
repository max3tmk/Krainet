package com.max.notification_service.dto;

import com.max.notification_service.enums.UserEventType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserEventNotificationRequest {

    @NotNull(message = "Event type must not be null")
    private UserEventType eventType;

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;

    private String password;
}
