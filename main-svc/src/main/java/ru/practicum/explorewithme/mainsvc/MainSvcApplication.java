package ru.practicum.explorewithme.mainsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.explorewithme.mainsvc", "ru.practicum.explorewithme.statsvc.client"})
public class MainSvcApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainSvcApplication.class, args);
    }
}
