package com.r2s.mobilestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FindInternshipApplication {
	public static void main(String[] args) {
		SpringApplication.run(FindInternshipApplication.class, args);
	}
}
