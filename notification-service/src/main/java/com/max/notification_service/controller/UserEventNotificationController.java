package com.max.notification_service.controller;

import com.max.notification_service.dto.UserEventNotificationRequest;
import com.max.notification_service.service.UserEventNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/internal/user-events")
@RequiredArgsConstructor
public class UserEventNotificationController {

    private final UserEventNotificationService userEventNotificationService;

    @PostMapping
    public ResponseEntity<Void> handleUserEvent(@Valid @RequestBody UserEventNotificationRequest request) {
        log.info("[USER-EVENT] Received user event: {}", request.getEventType());
        userEventNotificationService.notifyAdmins(request);
        return ResponseEntity.ok().build();
    }
}
