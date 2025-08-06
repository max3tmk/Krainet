package com.max.notification_service.service.impl;

import com.max.notification_service.repository.AdminEmailRepository;
import com.max.notification_service.service.AdminEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEmailServiceImpl implements AdminEmailService {

    private final AdminEmailRepository adminEmailRepository;

    @Override
    public List<String> getAdminEmails() {
        return adminEmailRepository.findAllAdminEmails();
    }
}
