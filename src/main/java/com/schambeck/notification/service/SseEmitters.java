package com.schambeck.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseEmitters {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void send(Object obj) {
        send(emitter -> emitter.send(obj));
    }

    private void send(SseEmitterConsumer<SseEmitter> consumer) {
        List<SseEmitter> failedEmitters = new ArrayList<>();
        if (emitters.isEmpty()) {
            log.debug("No active emitters");
        }
        emitters.forEach(emitter -> {
            try {
                consumer.accept(emitter);
            } catch (Exception e) {
                emitter.completeWithError(e);
                failedEmitters.add(emitter);
                log.error("Emitter failed: {}", emitter, e);
            }
        });
        emitters.removeAll(failedEmitters);
    }

    public SseEmitter add(SseEmitter emitter) {
        log.debug("Emitter added: {}", emitter);
        emitters.add(emitter);
        emitter.onCompletion(() -> {
            log.debug("Emitter completed: {}", emitter);
            emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.debug("Emitter timeout: {}", emitter);
            emitter.complete();
            emitters.remove(emitter);
        });
        return emitter;
    }

    @FunctionalInterface
    private interface SseEmitterConsumer<T> {
        void accept(T t) throws IOException;
    }

}
