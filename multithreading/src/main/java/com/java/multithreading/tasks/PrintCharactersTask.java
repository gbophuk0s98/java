package com.java.multithreading.tasks;

public class PrintCharactersTask implements Task {

    @Override
    public void solve() {
        PrintCharactersTask.CustomThread customThread = new PrintCharactersTask.CustomThread();
        PrintCharactersTask.CustomThread customThread1 = new PrintCharactersTask.CustomThread();

        customThread.start();
        customThread1.start();
    }

    public class CustomThread extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                System.out.printf("%s:%s%n", getName(), i + 1);
            }
        }
    }

}
