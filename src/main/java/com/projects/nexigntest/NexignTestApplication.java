package com.projects.nexigntest;

import com.projects.nexigntest.entities.Cdr;
import com.projects.nexigntest.repositories.CdrRepository;
import com.projects.nexigntest.services.CdrService;
import com.projects.nexigntest.services.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class NexignTestApplication {

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    CdrService cdrService;

    @Autowired
    CdrRepository cdrRepository;

    public static void main(String[] args) {
        SpringApplication.run(NexignTestApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            subscriberService.generate();
            cdrService.generate();
        };
    }
}
