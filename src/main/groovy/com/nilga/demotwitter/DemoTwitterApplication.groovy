package com.nilga.demotwitter

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Main class for the Demo Twitter Application.
 * This class serves as the entry point for the Spring Boot application.
 */
@SpringBootApplication
class DemoTwitterApplication {

	/**
	 * Main method to run the Demo Twitter Application.
	 *
	 * @param args command line arguments
	 */
	static void main(String[] args) {
		SpringApplication.run(DemoTwitterApplication, args)
	}
}
