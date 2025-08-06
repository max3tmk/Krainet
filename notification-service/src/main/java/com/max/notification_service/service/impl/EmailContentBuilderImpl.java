package com.max.notification_service.service.impl;

import com.max.notification_service.dto.UserEventNotificationRequest;
import com.max.notification_service.enums.UserEventType;
import com.max.notification_service.service.EmailContentBuilder;
import org.springframework.stereotype.Service;

@Service
public class EmailContentBuilderImpl implements EmailContentBuilder {

    @Override
    public String buildSubject(UserEventNotificationRequest request) {
        return String.format("%s пользователь %s", mapToRussian(request.getEventType()), request.getUsername());
    }

    @Override
    public String buildText(UserEventNotificationRequest request) {
        return String.format(
                "%s пользователь с именем - %s, паролем - %s и почтой - %s.",
                mapToRussian(request.getEventType()),
                request.getUsername(),
                request.getPassword() != null ? request.getPassword() : "[не указан]",
                request.getEmail()
        );
    }

    private String mapToRussian(UserEventType type) {
        return switch (type) {
            case CREATED -> "Создан";
            case UPDATED -> "Изменен";
            case DELETED -> "Удален";
        };
    }
}
