package com.KpotTipouts.KpotPay;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KpotPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(KpotPayApplication.class, args);
	}
	@Bean
	CommandLineRunner startupTest(UserRepository userRepository) {
		return args -> {
			String email = "abc";

			if(userRepository.findByEmail(email).isEmpty()) {
				User newUser = new User();
				newUser.setEmail(email);
				userRepository.save(newUser);
				System.out.println("User with email: " + email + " created!");
			}
			else {
				System.out.println("User with email: " + email + " already exists!");
			}
		};

	}
}
