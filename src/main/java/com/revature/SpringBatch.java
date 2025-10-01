package com.revature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class SpringBatch {

    public static void main(String[] args) {
        System.out.println("Args: " + Arrays.toString(args));
        SpringApplication.run(SpringBatch.class, args);
    }
}
