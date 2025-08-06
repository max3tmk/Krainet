package com.max.notification_service.service;

import jakarta.mail.MessagingException;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String text) throws MessagingException;
}
