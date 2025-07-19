package com.unravel.app.manager;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseManager {

	private final HikariDataSource hikariDataSource;

	public DatabaseManager(HikariDataSource hikariDataSource) {
		this.hikariDataSource = hikariDataSource;
	}

	public Connection getConnection() throws SQLException {
		return hikariDataSource.getConnection();
	}

	public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Scheduled(fixedDelay = 5000)
	public void monitorPool() {
		HikariPoolMXBean pool = hikariDataSource.getHikariPoolMXBean();
		if (pool == null) {
			System.err.println("[WARN] HikariPoolMXBean not available.");
			return;
		}

		int active = pool.getActiveConnections();
		int idle = pool.getIdleConnections();
		int total = pool.getTotalConnections();
		int waiting = pool.getThreadsAwaitingConnection();

		if (waiting > 0) {
			System.err.printf("[WARN] %d threads waiting for DB connections!%n", waiting);
		}

		if (idle > total * 0.7) {
			System.out.printf("[INFO] Pool underutilized. Idle: %d / Total: %d%n", idle, total);
		}

		if (waiting > 5 && hikariDataSource.getMaximumPoolSize() < 50) {
			int newSize = hikariDataSource.getMaximumPoolSize() + 5;
			hikariDataSource.setMaximumPoolSize(newSize);
			System.out.printf(" Increased maximumPoolSize to %d%n", newSize);
		}

		if (idle > total * 0.8) {
			hikariDataSource.setMinimumIdle(Math.max(5, hikariDataSource.getMinimumIdle() - 1));
			System.out.printf(" Reduced minimumIdle to %d%n", hikariDataSource.getMinimumIdle());
		}

		System.out.printf("[POOL METRICS] Active=%d, Idle=%d, Total=%d, Waiting=%d%n", active, idle, total, waiting);
	}
}
