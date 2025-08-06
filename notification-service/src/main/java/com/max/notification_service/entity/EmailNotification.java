package com.max.notification_service.entity;

import com.max.notification_service.enums.EmailNotificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Subject must not be blank")
    private String subject;

    @NotBlank(message = "Text must not be blank")
    @Column(columnDefinition = "TEXT")
    private String text;

    @NotBlank(message = "Recipient email must not be blank")
    @Email(message = "Invalid email format")
    private String recipientEmail;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private EmailNotificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
