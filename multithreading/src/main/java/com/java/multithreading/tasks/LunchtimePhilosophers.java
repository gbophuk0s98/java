package com.java.multithreading.tasks;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class LunchtimePhilosophers implements Task {

    @Override
    public void solve() {
        Semaphore semaphore = new Semaphore(2);

        Philosopher p1 = new Philosopher("Philosopher #1", semaphore);
        Philosopher p2 = new Philosopher("Philosopher #2", semaphore);
        Philosopher p3 = new Philosopher("Philosopher #3", semaphore);
        Philosopher p4 = new Philosopher("Philosopher #4", semaphore);
        Philosopher p5 = new Philosopher("Philosopher #5", semaphore);

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

        Fork getLeftFork();

        Fork getRightFork();

        boolean isEating();

        void ponder();

    }

    private interface ForkRequest {
        Fork getFork(Guest guest, boolean leftRequired) throws InterruptedException;
    }

    private static class DiningTable {
        private final ArrayList<Guest> guests;

        public DiningTable() {
            this.guests = new ArrayList<>();
        }

        public void putGuest(Guest guest) {
            guest.setSetting(new TableSetting());
            guest.requestFork(createForkRequest());

            guests.add(guest);
        }

        private ForkRequest createForkRequest() {
            return (guest, leftRequired) -> {
                int index = guests.indexOf(guest);

                if (leftRequired) {
                    index = index == 0 ? guests.size() - 1 : index - 1;
                }
                else {
                    index = index == guests.size() - 1 ? 0 : index + 1;
                }

                Guest tablemate = guests.get(index);

                while (tablemate.isEating()) {
                    guest.ponder();
                }

                Fork fork = leftRequired ? tablemate.getRightFork() : tablemate.getLeftFork();
                if (fork != null) {
                    System.out.printf("%s is sharing the %s fork with %s%n",
                        tablemate.getName(),
                        leftRequired ? "right" : "left",
                        guest.getName()
                    );
                }
                return fork;
            };
        }

    }

    private static class Philosopher extends Thread implements Guest {

        private final Semaphore semaphore;

        private TableSetting setting;

        private ForkRequest forkRequest;

        private boolean isEating;

        private Philosopher(String name, Semaphore semaphore) {
            setName(name);
            this.semaphore = semaphore;
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
        public Fork getLeftFork() {
            Fork fork = setting.getLeftFork();
            setting.setLeftFork(null);
            return fork;
        }

        @Override
        public Fork getRightFork() {
            Fork fork = setting.getRightFork();
            setting.setRightFork(null);
            return fork;
        }

        @Override
        public boolean isEating() {
            return isEating;
        }

        @Override
        public void ponder() {
            try {
                System.out.printf("%s is pondering...%n", getName());
                sleep(1000);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {
                eat();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void eat() throws InterruptedException {
            semaphore.acquire();

            while (!setting.isReady()) {
                if (setting.getLeftFork() == null) {
                    System.out.printf("%s is asking for the left fork%n", getName());
                    setting.setLeftFork(forkRequest.getFork(this, true));
                }

                if (setting.getRightFork() == null) {
                    System.out.printf("%s is asking for the right fork%n", getName());
                    setting.setRightFork(forkRequest.getFork(this, false));
                }
            }
            System.out.printf("%s has had the setting set: %s%n", getName(), setting.isReady());

            isEating = true;

            Plate plate = setting.getPlate();

            System.out.printf("%s started eating%n", getName());
            while (!plate.isEmpty()) {
                plate.decreaseFoodPercentage();
            }
            System.out.printf("%s finished eating%n", getName());

            isEating = false;
            semaphore.release();
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

