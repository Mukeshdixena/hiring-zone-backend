package com.hiringzone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HiringZoneBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiringZoneBackendApplication.class, args);
	}

}
