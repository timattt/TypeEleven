package io.mipt.typeeleven.grpc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan
@PropertySource("classpath:grpc.properties")
public class TypeElevenGrpcConfig {
}
