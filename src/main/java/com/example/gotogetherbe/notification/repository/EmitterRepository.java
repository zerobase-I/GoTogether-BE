package com.example.gotogetherbe.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId, event);
    }

    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

    public Map<String, SseEmitter> findAllEmitterStartWithByEmail(String email) {
        return emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(email))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findAllEventCacheStartWithByEmail(String email) {
        return eventCache.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(email))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteAllEmitterStartWithByEmail(String email) {
        emitters.forEach((key, value) -> {
            if(key.startsWith(email)) {
                emitters.remove(key);
            }
        });
    }

    public void deleteAllEventCacheStartWithByEmail(String email) {
        eventCache.forEach((key, value) -> {
            if(key.startsWith(email)) {
                eventCache.remove(key);
            }
        });
    }
}
