package com.java.multithreading.tasks;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class LunchtimePhilosophers implements Task {

    @Override
    public void solve() {
        Philosopher p1 = new Philosopher("Philosopher #1");
        Philosopher p2 = new Philosopher("Philosopher #2");
        Philosopher p3 = new Philosopher("Philosopher #3");
        Philosopher p4 = new Philosopher("Philosopher #4");
        Philosopher p5 = new Philosopher("Philosopher #5");

        DiningTable table = new DiningTable();
        table.putGuest(p1);
        table.putGuest(p2);
        table.putGuest(p3);
        table.putGuest(p4);
        table.putGuest(p5);

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
    }

    interface Guest {

        String getName();

        void setSetting(TableSetting setting);

        void requestFork(ForkRequest forkRequest);

        void setLeftFork(Fork fork);

        Fork getLeftFork();

        void setRightFork(Fork fork);

        Fork getRightFork();

        boolean isEating();

    }

    private interface ForkRequest {
        void askPolitely(Guest guest, boolean leftForkRequired);
    }

    private static class DiningTable {

        private final ArrayList<Guest> guests = new ArrayList<>();

        public void putGuest(Guest guest) {
            guest.setSetting(new TableSetting());
            guest.requestFork(createForkRequest());

            guests.add(guest);
        }

        private ForkRequest createForkRequest() {
            return (guest, leftForkRequired) -> {
                synchronized (guest) {
                    int index = guests.indexOf(guest);

                    if (leftForkRequired) {
                        index = index == guests.size() - 1 ? 0 : index + 1;
                    }
                    else {
                        index = index == 0 ? guests.size() - 1 : index - 1;
                    }

                    Guest tablemate = guests.get(index);

                    while (tablemate.isEating()) {
                        try {
                            wait();
                        }
                        catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (leftForkRequired) {
                        System.out.printf("%s is sharing the left fork with %s%n", tablemate.getName(), guest.getName());
                        guest.setRightFork(tablemate.getLeftFork());
                    }
                    else {
                        System.out.printf("%s is sharing the right fork with %s%n", tablemate.getName(), guest.getName());
                        guest.setLeftFork(tablemate.getRightFork());
                    }
                }
            };
        }

    }

    private static class Philosopher extends Thread implements Guest {

        private final AtomicBoolean isEating;

        private TableSetting setting;

        private ForkRequest forkRequest;

        private Philosopher(String name) {
            setName(name);
            this.isEating = new AtomicBoolean(false);
        }

        @Override
        public void setSetting(TableSetting setting) {
            this.setting = setting;
        }

        @Override
        public void requestFork(ForkRequest forkRequest) {
            this.forkRequest = forkRequest;
        }

        @Override
        public synchronized void setLeftFork(Fork fork) {
            setting.setLeftFork(fork);
        }

        @Override
        public synchronized Fork getLeftFork() {
            Fork fork = setting.getLeftFork();
            setting.setLeftFork(null);
            return fork;
        }

        @Override
        public void setRightFork(Fork fork) {
            setting.setRightFork(fork);
        }

        @Override
        public synchronized Fork getRightFork() {
            Fork fork = setting.getRightFork();
            setting.setRightFork(null);
            return fork;
        }

        @Override
        public void run() {
            eat();
        }

        public void eat() {
            System.out.printf("%s is asking for the right fork%n", getName());
            forkRequest.askPolitely(this, true);
//            while (!setting.isReady()) {
//                if (setting.getLeftFork() == null) {
//                    System.out.printf("%s is asking for the left fork%n", getName());
//                    forkRequest.askPolitely(this, true);
//                }
//
//                if (setting.getRightFork() == null) {
//                    System.out.printf("%s is asking for the right fork%n", getName());
//                    forkRequest.askPolitely(this, true);
//                }
//            }

            isEating.set(true);
            System.out.printf(String.format("%s started eating\n", getName()));

            Plate plate = setting.getPlate();
            while (!plate.isEmpty()) {
                plate.decreaseFoodPercentage();
            }

            isEating.set(false);
            System.out.printf(String.format("%s finished eating\n", getName()));
            notify();
        }

        public boolean isEating() {
            return isEating.get();
        }
    }

    private static class TableSetting {

        private final Plate plate;

        private Fork leftFork;

        private Fork rightFork;

        private TableSetting() {
            this.plate = new Plate();
            this.leftFork = new Fork();
        }

        public Plate getPlate() {
            return plate;
        }

        public Fork getLeftFork() {
            return leftFork;
        }

        public void setLeftFork(Fork leftFork) {
            this.leftFork = leftFork;
        }

        public Fork getRightFork() {
            return rightFork;
        }

        public void setRightFork(Fork rightFork) {
            this.rightFork = rightFork;
        }

        public boolean isReady() {
            return this.leftFork != null && this.rightFork != null;
        }
    }

    private static class Plate {
        private int foodPercentage = 100;

        public void decreaseFoodPercentage() {
            if (isEmpty()) {
                throw new RuntimeException("Nothing to eat");
            }
            foodPercentage -= 10;
        }

        public boolean isEmpty() {
            return foodPercentage == 0;
        }

    }

    private static class Fork {

    }

}
