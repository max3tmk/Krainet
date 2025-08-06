package com.max.notification_service.service.impl;

import com.max.notification_service.service.EmailSenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String text) throws MessagingException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            log.info("[EMAIL] Email sent to {}", to);
        } catch (Exception e) {
            log.error("[EMAIL] Failed to send email to {}: {}", to, e.getMessage());
            throw new MessagingException("Failed to send email", e);
        }
    }
}
