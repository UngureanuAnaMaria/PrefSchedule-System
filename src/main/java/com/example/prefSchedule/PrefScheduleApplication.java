package com.example.prefSchedule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PrefScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrefScheduleApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner run(DataPopulator dataPopulator) {
//		return args -> {
//			//dataPopulator.populateDatabase();
//			dataPopulator.testCompleteFlow();
//		};
//	}

	@Bean
	public CommandLineRunner run(TestDataPopulator dataPopulator) {
		return args -> {
			try {
				dataPopulator.generateFullMatchingScenario();
			} catch (Exception e) {
				System.err.println("Error during data population: " + e.getMessage());
				System.out.println("Application started - you can manually register users via API");
			}
		};
	}

//  SAU--------------------------------------------------------------------------------------------------------------------------------------------------
//	@Component
//	public class DatabaseRunner implements CommandLineRunner {
//
//	    @Autowired
//	    private DataPopulator dataPopulator;
//
//	    @Override
//		public void run(String... args) throws Exception {
//			dataPopulator.populateDatabase();
//		}
//	}
}
