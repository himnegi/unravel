package com.unravel.app;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockSafe {
	private final ReentrantLock lock1 = new ReentrantLock(true);
	private final ReentrantLock lock2 = new ReentrantLock(true);

	public void method1() {
		try {
			if (lock1.tryLock(500, TimeUnit.MILLISECONDS)) {
				try {
					if (lock2.tryLock(500, TimeUnit.MILLISECONDS)) {
						try {
							System.out.println("method1 safely acquired lock1 then lock2");
						} finally {
							lock2.unlock();
						}
					} else {
						System.out.println("method1 could not acquire lock2 in time, avoiding deadlock");
					}
				} finally {
					lock1.unlock();
				}
			} else {
				System.out.println("method1 could not acquire lock1 in time, avoiding deadlock");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("method1 interrupted while waiting for locks");
		}
	}

	public void method2() {
		try {
			if (lock1.tryLock(500, TimeUnit.MILLISECONDS)) {
				try {
					if (lock2.tryLock(500, TimeUnit.MILLISECONDS)) {
						try {
							System.out.println("method2 safely acquired lock1 then lock2");
						} finally {
							lock2.unlock();
						}
					} else {
						System.out.println("method2 could not acquire lock2 in time, avoiding deadlock");
					}
				} finally {
					lock1.unlock();
				}
			} else {
				System.out.println("method2 could not acquire lock1 in time, avoiding deadlock");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("method2 interrupted while waiting for locks");
		}
	}
}
