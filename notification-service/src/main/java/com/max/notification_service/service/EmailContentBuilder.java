package com.max.notification_service.service;

import com.max.notification_service.dto.UserEventNotificationRequest;

public interface EmailContentBuilder {
    String buildSubject(UserEventNotificationRequest request);
    String buildText(UserEventNotificationRequest request);
}
