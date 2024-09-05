package io.mipt.typeeleven.starter;

import io.mipt.typesix.businesslogic.TypeSixBusinessLogicConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
@SpringBootApplication(exclude = { TypeSixBusinessLogicConfig.class })
public class TypeElevenStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(TypeElevenStarterApplication.class, args);
    }

}
