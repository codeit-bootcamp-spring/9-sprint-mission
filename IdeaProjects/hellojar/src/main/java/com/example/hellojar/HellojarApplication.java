package com.example.hellojar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class HellojarApplication {

    public static void main(String[] args) {

        SpringApplication.run(HellojarApplication.class, args);
    }

    @GetMapping("/")
    public String hello() {
        return "Hello, Executable Jar!";
    }

    @GetMapping("/info")
    public String info() {
        return String.format(
                "Java Version: %s, Spring Boot: %s",
                System.getProperty("java.version"),
                HellojarApplication.class.getPackage().getImplementationVersion()
        );
    }

}
