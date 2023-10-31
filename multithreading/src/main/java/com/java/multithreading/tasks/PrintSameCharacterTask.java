package com.java.multithreading.tasks;

public class PrintSameCharacterTask implements Task {

    @Override
    public void solve() {
        StringBuilder builder = new StringBuilder();
        builder.append('a');

        new CustomThread(builder).start();
        new CustomThread(builder).start();
        new CustomThread(builder).start();

    }

    private class CustomThread extends Thread {

        private final StringBuilder builder;

        private CustomThread(StringBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void run() {
            System.out.println(getName() + "\n");

            synchronized (builder) {
                System.out.printf("%s is working%n", getName());
                for (int i = 0; i < 10; i++) {
                    System.out.println(builder);
                }

                builder.append('a');
            }
        }

    }

}
