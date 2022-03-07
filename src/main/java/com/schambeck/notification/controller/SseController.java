package com.schambeck.notification.controller;

import com.schambeck.notification.representation.CountUnreadMessage;
import com.schambeck.notification.service.NotificationService;
import com.schambeck.notification.service.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Slf4j
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
class SseController {

    private static final long TIMEOUT = 60000L;
    private final SseEmitters emitters;
    private final NotificationService service;

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    SseEmitter stream() {
        SseEmitter emitter = emitters.add(new SseEmitter(TIMEOUT));
        long countUnread = service.countByReadIsFalse();
        sendNotification(countUnread);
        return emitter;
    }

    private void sendNotification(long countUnread) {
        CountUnreadMessage message = CountUnreadMessage.builder()
                .countUnread(countUnread)
                .build();
        emitters.send(message);
        log.info("Message sent: {}", message);
    }

}
