package com.unravel.app.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

public class MemoryManager {
    private static final Cache<String, byte[]> sessionCache = Caffeine.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public static void addSessionData(String sessionId) {
        sessionCache.put(sessionId, new byte[10 * 1024 * 1024]);
    }

    public static void removeSessionData(String sessionId) {
        sessionCache.invalidate(sessionId);
    }
}
