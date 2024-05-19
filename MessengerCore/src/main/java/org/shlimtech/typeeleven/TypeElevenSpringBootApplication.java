package org.shlimtech.typeeleven;

import org.shlimtech.typesixbusinesslogic.EnableTypeSixBusinessLogic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableTypeSixBusinessLogic
@EnableJpaRepositories(basePackages = "org.shlimtech")
@EntityScan(basePackages = "org.shlimtech")
public class TypeElevenSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TypeElevenSpringBootApplication.class, args);
	}

}
