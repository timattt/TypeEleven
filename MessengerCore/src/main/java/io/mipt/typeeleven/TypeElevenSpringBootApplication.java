package io.mipt.typeeleven;

import io.mipt.typesix.businesslogic.EnableTypeSixBusinessLogic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableTypeSixBusinessLogic
@EnableJpaRepositories(basePackages = "io.mipt")
@EntityScan(basePackages = "io.mipt")
public class TypeElevenSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TypeElevenSpringBootApplication.class, args);
	}

}
