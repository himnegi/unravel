package com.unravel.app.manager;

import java.util.UUID;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class SessionManager {
    private final RedisCommands<String, String> redis;

    public SessionManager(String redisUri) {
        RedisClient client = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = client.connect();
        this.redis = connection.sync();
    }

    public String login(String userId) {
        String key = "session:" + userId;
        String existing = redis.get(key);
        if (existing != null) return   String.format("User already logged in, user %s. Session ID: %s", userId, existing);
        String sessionId = "SESSION_" + UUID.randomUUID();
        redis.set(key, sessionId);
        MemoryManager.addSessionData(sessionId);
        return  String.format("Login successful for user %s. Session ID: %s", userId, sessionId);
    }

    public String logout(String userId) {
        String key = "session:" + userId;
        String existing = redis.get(key);
        if (existing == null) return "User not logged in.";
        redis.del(key);
        MemoryManager.removeSessionData(existing);
        return "Logout successful.";
    }

    public String getSessionDetails(String userId) {
        String sessionId = redis.get("session:" + userId);
        if (sessionId == null) throw new RuntimeException("Session not found for user " + userId);
        return String.format("Session ID for user %s: %s", userId, sessionId);
    }
}
