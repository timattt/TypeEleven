package io.mipt.typeeleven;

import lombok.extern.java.Log;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@Log
@SpringBootTest
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.profiles.active=test",
        // H2 DATA BASE
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.r2dbc.url=r2dbc:h2:mem:///~/db/postgres;MODE=PostgreSQL",
        "spring.r2dbc.username=tmp_user",
        "spring.r2dbc.password=tmp_password",
        "logging.level.org.springframework.data.r2dbc=DEBUG",
        "spring.r2dbc.properties.ssl=false",
        // LIQUIBASE
        "spring.liquibase.url=jdbc:h2:mem:~/db/postgres;DB_CLOSE_DELAY=-1",
        "spring.liquibase.user=tmp_user",
        "spring.liquibase.password=tmp_password",
})
public class BaseTest {
}
