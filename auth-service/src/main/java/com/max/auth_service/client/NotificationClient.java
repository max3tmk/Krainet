package com.max.auth_service.client;

import com.max.auth_service.dto.UserEventNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notification-service.url}")
    private String notificationServiceUrl;

    public void notifyUserEvent(UserEventNotificationRequest request) {
        try {
            log.info("NotificationClient] Sending notification to URL: {}", notificationServiceUrl);
            ResponseEntity<Void> response = restTemplate.postForEntity(notificationServiceUrl, request, Void.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("[NotificationClient] Successfully notified notification-service of user event: {}", request.getEventType());
            } else {
                log.warn("[NotificationClient] Notification-service returned non-OK status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("[NotificationClient] Failed to notify notification-service: {}", e.getMessage(), e);
        }
    }
}
