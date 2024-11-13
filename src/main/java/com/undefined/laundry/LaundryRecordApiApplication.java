package com.undefined.laundry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LaundryRecordApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LaundryRecordApiApplication.class, args);
	}

}
