package com.schambeck.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseEmitters {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void send(String userId, Object obj) {
        send(userId, emitter -> emitter.send(obj));
    }

    private void send(String userId, SseEmitterConsumer<SseEmitter> consumer) {
        List<SseEmitter> failedEmitters = new ArrayList<>();
        if (emitters.isEmpty()) {
            log.debug("No active emitters");
        }
        List<SseEmitter> emitterList = emitters.get(userId);
        emitterList.forEach(emitter -> {
            try {
                consumer.accept(emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
                failedEmitters.add(emitter);
                log.error("Emitter failed: {}", emitter, e);
            }
        });
        failedEmitters.forEach(emitter -> removeEmitter(emitter, emitterList, userId));
    }

    public SseEmitter add(String userId, SseEmitter emitter) {
        log.debug("Emitter added: {}", emitter);
        if (!emitters.containsKey(userId)) {
            emitters.put(userId, new CopyOnWriteArrayList<>());
        }
        List<SseEmitter> emitterList = emitters.get(userId);
        emitterList.add(emitter);
        emitter.onCompletion(() -> {
            log.debug("Emitter completed: {}", emitter);
            removeEmitter(emitter, emitterList, userId);
        });
        emitter.onTimeout(() -> {
            log.debug("Emitter timeout: {}", emitter);
            emitter.complete();
            removeEmitter(emitter, emitterList, userId);
        });
        return emitter;
    }

    private void removeEmitter(SseEmitter emitter, List<SseEmitter> emitterList, String userId) {
        emitterList.remove(emitter);
        if (emitterList.isEmpty()) {
            emitters.remove(userId);
        }
    }

    @FunctionalInterface
    private interface SseEmitterConsumer<T> {
        void accept(T t) throws IOException;
    }

}
