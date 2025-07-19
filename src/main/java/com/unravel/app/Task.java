package com.unravel.app;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

class Task implements Comparable<Task> {
	private final String name;
	private final int priority;

	public Task(String name, int priority) {
		this.name = name;
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Task other) {
		return Integer.compare(this.priority, other.priority);
	}
}

class TaskProcessor {
	private final BlockingQueue<Task> queue = new PriorityBlockingQueue<>();

	public void produce(Task t) throws InterruptedException {
		queue.put(t);
		System.out.println("Produced: " + t.getName());
	}

	public Task consume() throws InterruptedException {
		return queue.take();
	}
}

class Producer extends Thread {
	private final TaskProcessor processor;

	public Producer(TaskProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void run() {
		try {
			processor.produce(new Task("Critical-1", 1));
			processor.produce(new Task("Normal-1", 5));
		} catch (InterruptedException ignored) {
		}
	}
}

class Consumer extends Thread {
	private final TaskProcessor processor;

	public Consumer(TaskProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Task t = processor.consume();
				System.out.println("Consumed: " + t.getName());
			}
		} catch (InterruptedException ignored) {
		}
	}
}
