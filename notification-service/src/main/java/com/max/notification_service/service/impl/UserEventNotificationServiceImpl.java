package com.max.notification_service.service.impl;

import com.max.notification_service.dto.UserEventNotificationRequest;
import com.max.notification_service.entity.EmailNotification;
import com.max.notification_service.enums.EmailNotificationStatus;
import com.max.notification_service.repository.EmailNotificationRepository;
import com.max.notification_service.service.AdminEmailService;
import com.max.notification_service.service.EmailContentBuilder;
import com.max.notification_service.service.EmailSenderService;
import com.max.notification_service.service.UserEventNotificationService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventNotificationServiceImpl implements UserEventNotificationService {

    private final AdminEmailService adminEmailService;
    private final EmailContentBuilder emailContentBuilder;
    private final EmailSenderService emailSenderService;
    private final EmailNotificationRepository emailNotificationRepository;

    @Override
    @Transactional
    public void notifyAdmins(@Valid UserEventNotificationRequest request) {
        log.info("[USER-EVENT] Event received: {}", request);

        List<String> adminEmails = adminEmailService.getAdminEmails();

        if (adminEmails.isEmpty()) {
            log.warn("[USER-EVENT] No admins found to notify");
            return;
        }

        for (String email : adminEmails) {
            String subject = emailContentBuilder.buildSubject(request);
            String text = emailContentBuilder.buildText(request);

            EmailNotification notification = EmailNotification.builder()
                    .recipientEmail(email)
                    .subject(subject)
                    .text(text)
                    .createdAt(LocalDateTime.now())
                    .status(EmailNotificationStatus.PENDING)
                    .build();

            emailNotificationRepository.save(notification);

            try {
                emailSenderService.sendEmail(email, subject, text);
                notification.setStatus(EmailNotificationStatus.SENT);
                notification.setErrorMessage(null);
                log.info("[USER-EVENT] Email sent successfully to {}", email);
            } catch (MessagingException e) {
                notification.setStatus(EmailNotificationStatus.FAILED);
                notification.setErrorMessage(e.getMessage());
                log.error("[USER-EVENT] Failed to send email to {}: {}", email, e.getMessage());
            }

            emailNotificationRepository.save(notification);
        }

        log.info("[USER-EVENT] Notifications processed for {} admin(s)", adminEmails.size());
    }
}
