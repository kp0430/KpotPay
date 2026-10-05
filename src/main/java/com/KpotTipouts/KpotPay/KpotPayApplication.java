package com.KpotTipouts.KpotPay;

import com.KpotTipouts.KpotPay.Entity.User;
import com.KpotTipouts.KpotPay.Repository.ShiftRepository;
import com.KpotTipouts.KpotPay.Repository.UserRepository;
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


			  /*   User user = userRepository.findByEmail(email).get();
			Shift testShift = new Shift();
			testShift.setDate(LocalDate.of(2026,9,26));
			testShift.setUser(user);
			testShift.setRole(Role.SERVER);
			testShift.setBarSales(BigDecimal.valueOf(200));
			testShift.setHoursWorked(BigDecimal.valueOf(6.3));
			testShift.setFoodSales(BigDecimal.valueOf(1000.50));
			testShift.setTips(BigDecimal.valueOf(230.14));
			shiftRepository.save(testShift);
			*/
		};

	}
}
