package io.mipt.typeeleven.web.grpc;

import io.grpc.*;
import io.grpc.stub.MetadataUtils;
import io.mipt.typeeleven.grpc.MessengerGrpc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
public class TestClientConfig {
    private static final String TOKEN = "eyJraWQiOiJ0eXBlLTYta2V5LWlkIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJhQGdtYWlsLmNvbSIsImF1ZCI6InR5cGUxMi1jbGllbnQiLCJuYmYiOjE3MTk2NDg1MDcsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6Nzc3NyIsImlkIjoiMSIsImV4cCI6MTcxOTY0ODgwNywiaWF0IjoxNzE5NjQ4NTA3LCJqdGkiOiJkZWYwNTBiMi1jZWU0LTQwODYtOWVjMC1iNGUzMTEzY2IyMGQiLCJlbWFpbCI6ImFAZ21haWwuY29tIn0.EImIIAZ4A_E0BDhYIIUC3RJu6NXSEYWVoKK0BD-OXuUwuD8duqu89U_i6Ei2RFRGAGiWQBN5xNumOJH6tEsNW8knKOdeaM-gbg9P1m0VrPCWh22Rpcj1-_khEjnnF1jIztsbOjMrfNQ3B-9Fw8zGiFbE5uIvCP81WO8uARwCEzOvTu9GEeMt0gvJ5dEZN5K6Mhb2ikbImiXLZilX7uoXMyPdvWmnphsmaANjnAV7zYaAT6mE14aLm5Y56hZJHsHYmCyqLSkeZZDvN6T-HffsClgVGt5Y38ZwkDC3doAouRpuMeBeWxUzyay5NAbAHrPY5gKdVa8Q2rJpJifVN36uzQ";

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(TOKEN).header("Authorization", "Bearer " + TOKEN).claim("id", 1).build();
    }

    @Bean
    @Qualifier("securedClient")
    public MessengerGrpc.MessengerBlockingStub securedClient() {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + TOKEN);
        ClientInterceptor interceptor = MetadataUtils.newAttachHeadersInterceptor(metadata);
        return MessengerGrpc.newBlockingStub(
                ClientInterceptors.intercept(
                        ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build(),
                        interceptor
                ));
    }

    @Bean
    @Qualifier("unsecuredClient")
    public MessengerGrpc.MessengerBlockingStub unsecuredClient() {
        return MessengerGrpc.newBlockingStub(
                ClientInterceptors.intercept(
                        ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build()
                ));
    }
}
