package com.max.notification_service.repository;

import com.max.notification_service.entity.AdminUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmailNotificationRepository extends CrudRepository<AdminUser, Long> {

    @Query(value = "SELECT email FROM users WHERE role = 'ROLE_ADMIN'", nativeQuery = true)
    List<String> findAllAdminEmails();
}
