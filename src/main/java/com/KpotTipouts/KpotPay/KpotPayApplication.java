package com.KpotTipouts.KpotPay;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class KpotPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(KpotPayApplication.class, args);
	}
	@Bean
	CommandLineRunner startupTest(UserRepository userRepository, ShiftRepository shiftRepository) {
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


			     /*User user = userRepository.findByEmail(email).get();
			Shift testShift = new Shift();
			testShift.setDate(LocalDate.now());
			testShift.setUser(user);
			testShift.setRole(Role.SERVER);
			testShift.setBarSales(BigDecimal.valueOf(100.23));
			testShift.setHoursWorked(BigDecimal.valueOf(6.20));
			testShift.setFoodSales(BigDecimal.valueOf(900.53));
			testShift.setTips(BigDecimal.valueOf(60.50));
			shiftRepository.save(testShift);
			*/
		};

	}
}
