package com.max.notification_service.service;

import com.max.notification_service.dto.UserEventNotificationRequest;

public interface UserEventNotificationService {
    void notifyAdmins(UserEventNotificationRequest request);
}
