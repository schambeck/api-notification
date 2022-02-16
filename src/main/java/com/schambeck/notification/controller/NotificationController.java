package com.schambeck.notification.controller;

import com.schambeck.notification.domain.Notification;
import com.schambeck.notification.representation.CountUnreadMessage;
import com.schambeck.notification.service.NotificationService;
import com.schambeck.notification.service.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

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
    Notification create(@RequestBody Notification notification) {
        Notification created = service.create(notification);
        long countUnread = service.countByReadIsFalse();
        sendNotification(countUnread, created);
        return created;
    }

    @PutMapping("/{id}/actions/mark-as-read")
    @ResponseStatus(OK)
    CountUnreadMessage markAsRead(@PathVariable("id") UUID id) {
        long countUnread = service.markAsReadAndCount(id);
        return sendNotification(countUnread);
    }

    @GetMapping("/queries/count-unread")
    @ResponseStatus(OK)
    Map<String, Long> countUnread() {
        Map<String, Long> map = new HashMap<>();
        map.put("countUnread", service.countByReadIsFalse());
        return map;
    }

    @GetMapping
    @ResponseStatus(OK)
    List<Notification> findAll() {
        return service.findAll();
    }

    private CountUnreadMessage sendNotification(long countUnread) {
        return sendNotification(countUnread, null);
    }

    private CountUnreadMessage sendNotification(long countUnread, Notification notification) {
        CountUnreadMessage message = CountUnreadMessage.builder()
                .countUnread(countUnread)
                .notification(notification)
                .build();
        emitters.send(message);
        log.info("Message sent: {}", message);
        return message;
    }

}
