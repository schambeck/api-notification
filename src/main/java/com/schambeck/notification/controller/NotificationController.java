package com.schambeck.notification.controller;

import com.schambeck.notification.domain.Notification;
import com.schambeck.notification.representation.CountUnreadMessage;
import com.schambeck.notification.service.NotificationService;
import com.schambeck.notification.service.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
class NotificationController {

    private final NotificationService service;
    private final SseEmitters emitters;

    @PostMapping
    @ResponseStatus(CREATED)
    Notification create(@RequestBody Notification notification, Principal principal) {
        Notification created = service.create(notification, principal.getName());
        long countUnread = service.countUnread(principal.getName());
        sendNotification(principal.getName(), countUnread, created);
        return created;
    }

    @PutMapping("/{id}/actions/mark-as-read")
    @ResponseStatus(OK)
    CountUnreadMessage markAsRead(@PathVariable("id") UUID id, Principal principal) {
        long countUnread = service.markAsReadAndCount(id, principal.getName());
        return sendNotification(principal.getName(), countUnread);
    }

    @GetMapping("/queries/count-unread")
    @ResponseStatus(OK)
    CountUnreadMessage countUnread(Principal principal) {
        return CountUnreadMessage.builder()
                .countUnread(service.countUnread(principal.getName()))
                .build();
    }

    @GetMapping("/queries/find-by-user")
    @ResponseStatus(OK)
    List<Notification> findByUserId(Principal principal) {
        return service.findByUserId(principal.getName());
    }

    private CountUnreadMessage sendNotification(String userId, long countUnread) {
        return sendNotification(userId, countUnread, null);
    }

    private CountUnreadMessage sendNotification(String userId, long countUnread, Notification notification) {
        CountUnreadMessage message = CountUnreadMessage.builder()
                .countUnread(countUnread)
                .notification(notification)
                .build();
        emitters.send(userId, message);
        log.info("Message sent to {}: {}", userId, message);
        return message;
    }

}
