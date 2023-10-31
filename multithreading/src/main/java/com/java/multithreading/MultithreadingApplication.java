package com.java.multithreading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.java.multithreading.tasks.LunchtimePhilosophers;

@SpringBootApplication
public class MultithreadingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultithreadingApplication.class, args);

        new LunchtimePhilosophers().solve();
    }

}
