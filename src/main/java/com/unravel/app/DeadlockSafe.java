package com.unravel.app;

public class DeadlockSafe {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void method1() {
        synchronized (lock1) {
            synchronized (lock2) {
                System.out.println("method1 acquired lock1 then lock2");
            }
        }
    }

    public void method2() {
        synchronized (lock1) {
            synchronized (lock2) {
                System.out.println("method2 acquired lock1 then lock2");
            }
        }
    }
}
