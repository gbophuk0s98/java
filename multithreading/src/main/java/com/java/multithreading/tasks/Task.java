package com.java.multithreading.tasks;

public interface Task {

    void solve();

    default void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
