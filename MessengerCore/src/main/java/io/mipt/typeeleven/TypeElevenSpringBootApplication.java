package io.mipt.typeeleven;

import io.mipt.typesix.businesslogic.TypeSixBusinessLogicConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EntityScan
@EnableR2dbcRepositories
@SpringBootApplication(exclude = { TypeSixBusinessLogicConfig.class })
public class TypeElevenSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TypeElevenSpringBootApplication.class, args);
	}

}
