package com.java.multithreading.tasks;

public class ImplementRunnableTask implements Task {

    @Override
    public void solve() {
        new Thread(new RunnableImpl(500)).start();
        new Thread(new RunnableImpl(1000)).start();
    }

    private class RunnableImpl implements Runnable {

        private final int delay;

        public RunnableImpl(int delay) {
            this.delay = delay;
        }

        @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    if (i % 10 == 0) {
                        System.out.printf("%s:%s%n", Thread.currentThread().getName(), i);
                        sleep(delay);
                    }
                }
            }

        }

}
