package com.unravel.app.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class MemoryManager {

	private static final Cache<String, byte[]> sessionCache = Caffeine.newBuilder()
			.expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(1000).recordStats().build();

	public static void addSessionData(String sessionId) {
		try {
			byte[] data = new byte[10 * 1024 * 1024];
			sessionCache.put(sessionId, data);
			logCacheStats("Added session: " + sessionId);
		} catch (OutOfMemoryError oom) {
			System.out.println("OOM while adding session data for " + sessionId);
			oom.printStackTrace();
		}
	}

	public static void removeSessionData(String sessionId) {
		sessionCache.invalidate(sessionId);
		logCacheStats("Removed session: " + sessionId);
	}

	private static void logCacheStats(String action) {
		System.out.println(action + " | Current cache size: " + sessionCache.estimatedSize());
	}

	public static void debugMemory() {
		System.out.println("Cache Stats: " + sessionCache.stats());
		System.out.println(" Estimated Cache Size: " + sessionCache.estimatedSize());
	}
}
