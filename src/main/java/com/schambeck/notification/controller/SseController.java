package com.schambeck.notification.controller;

import com.schambeck.notification.representation.CountUnreadMessage;
import com.schambeck.notification.service.NotificationService;
import com.schambeck.notification.service.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Slf4j
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://schambeck.github.io/ui-dna"})
class SseController {

    private static final long TIMEOUT = 60000L;
    private final SseEmitters emitters;
    private final NotificationService service;
    private final JwtDecoder jwtDecoder;

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    SseEmitter stream(@RequestParam String token) {
        Jwt jwt = jwtDecoder.decode(token);
        String userId = jwt.getClaimAsString("sub");
        SseEmitter emitter = emitters.add(userId, new SseEmitter(TIMEOUT));
        long countUnread = service.countUnread(userId);
        sendNotification(userId, countUnread);
        return emitter;
    }

    private void sendNotification(String userId, long countUnread) {
        CountUnreadMessage message = CountUnreadMessage.builder()
                .countUnread(countUnread)
                .build();
        emitters.send(userId, message);
        log.info("Initial Message sent to {}: {}", userId, message);
    }

}
