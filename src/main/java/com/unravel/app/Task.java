package com.unravel.app;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.*;

class Task implements Comparable<Task> {
	private static final long STARVATION_THRESHOLD_MS = 5000;
	private static final int STARVATION_PRIORITY_BOOST = 1;

	private static final AtomicLong sequencer = new AtomicLong();
	final long sequenceNumber;
	final String type;
	final int basePriority;
	final long createdTime;

	public Task(String type, int priority) {
		this.type = type;
		this.basePriority = priority;
		this.createdTime = System.currentTimeMillis();
		this.sequenceNumber = sequencer.getAndIncrement();
	}

	private int effectivePriority() {
		long age = System.currentTimeMillis() - createdTime;
		int priority = basePriority;
		if (age > STARVATION_THRESHOLD_MS) {
			priority = Math.max(0, basePriority - STARVATION_PRIORITY_BOOST);
		}
		return priority;
	}

	@Override
	public int compareTo(Task other) {
		int p1 = this.effectivePriority();
		int p2 = other.effectivePriority();
		if (p1 != p2) {
			return Integer.compare(p1, p2);
		}
		return Long.compare(this.sequenceNumber, other.sequenceNumber);
	}

	@Override
	public String toString() {
		return String.format("[Type=%s, Priority=%d, Created=%d]", type, basePriority, createdTime);
	}
}

class TaskQueue {
	private final PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>();

	public void submit(Task task) {
		queue.put(task);
		System.out.println("Produced: " + task);
	}

	public Task take() throws InterruptedException {
		return queue.take();
	}
}

class ProducerThread extends Thread {
	private final TaskQueue taskQueue;
	private final Random random = new Random();
	private final String[] types = { "CRITICAL", "NORMAL", "LOW" };

	public ProducerThread(TaskQueue queue) {
		this.taskQueue = queue;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < 20; i++) {
				int idx = random.nextInt(types.length);
				String type = types[idx];
				int priority;
				switch (type) {
				case "CRITICAL":
					priority = 0;
					break;
				case "NORMAL":
					priority = 1;
					break;
				default:
					priority = 2;
					break;
				}
				Task task = new Task(type, priority);
				taskQueue.submit(task);
				Thread.sleep(random.nextInt(200));
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}

class ConsumerWorker implements Runnable {
	private final TaskQueue taskQueue;

	public ConsumerWorker(TaskQueue queue) {
		this.taskQueue = queue;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Task task = taskQueue.take();
				System.out.println(Thread.currentThread().getName() + " Consuming: " + task);
				// simulate processing time
				Thread.sleep(300);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static void main(String[] args) {
		TaskQueue taskQueue = new TaskQueue();

		// Start multiple producers
		for (int i = 0; i < 2; i++) {
			new ProducerThread(taskQueue).start();
		}

		int consumerCount = 3;
		ExecutorService consumers = Executors.newFixedThreadPool(consumerCount);
		for (int i = 0; i < consumerCount; i++) {
			consumers.submit(new ConsumerWorker(taskQueue));
		}

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			consumers.shutdownNow();
		}

	}
}
