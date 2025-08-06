package com.max.notification_service.repository;

import com.max.notification_service.entity.EmailNotification;
import org.springframework.data.repository.CrudRepository;

public interface EmailNotificationRepository extends CrudRepository<EmailNotification, Long> {
}
