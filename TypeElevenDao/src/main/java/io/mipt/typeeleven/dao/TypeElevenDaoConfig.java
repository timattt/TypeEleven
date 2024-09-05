package io.mipt.typeeleven.dao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EntityScan
@ComponentScan
@Configuration
@EnableR2dbcRepositories
@PropertySource("classpath:db.properties")
public class TypeElevenDaoConfig {
}
