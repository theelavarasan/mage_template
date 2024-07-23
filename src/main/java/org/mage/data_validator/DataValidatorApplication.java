package org.mage.data_validator;

import org.mage.data_validator.service.TemplateDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@EnableSwagger2
@Component
//@EnableOpenApi
public class DataValidatorApplication {


	public static void main(String[] args) {
		SpringApplication.run(DataValidatorApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner run(TemplateDataService templateDataService) {
//		return args -> {
//			templateDataService.ExecuteSQLFile();
////			templateService.connectOddDatabase();
//		};
//	}

}
