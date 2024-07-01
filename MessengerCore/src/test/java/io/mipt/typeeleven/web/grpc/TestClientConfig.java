package io.mipt.typeeleven.web.grpc;

import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.mipt.typeeleven.grpc.MessengerGrpc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class TestClientConfig {
    static final String TOKEN = "eyJraWQiOiJ0eXBlLTYta2V5LWlkIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJhQGdtYWlsLmNvbSIsImF1ZCI6InR5cGUxMi1jbGllbnQiLCJuYmYiOjE3MTk2NDg1MDcsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6Nzc3NyIsImlkIjoiMSIsImV4cCI6MTcxOTY0ODgwNywiaWF0IjoxNzE5NjQ4NTA3LCJqdGkiOiJkZWYwNTBiMi1jZWU0LTQwODYtOWVjMC1iNGUzMTEzY2IyMGQiLCJlbWFpbCI6ImFAZ21haWwuY29tIn0.EImIIAZ4A_E0BDhYIIUC3RJu6NXSEYWVoKK0BD-OXuUwuD8duqu89U_i6Ei2RFRGAGiWQBN5xNumOJH6tEsNW8knKOdeaM-gbg9P1m0VrPCWh22Rpcj1-_khEjnnF1jIztsbOjMrfNQ3B-9Fw8zGiFbE5uIvCP81WO8uARwCEzOvTu9GEeMt0gvJ5dEZN5K6Mhb2ikbImiXLZilX7uoXMyPdvWmnphsmaANjnAV7zYaAT6mE14aLm5Y56hZJHsHYmCyqLSkeZZDvN6T-HffsClgVGt5Y38ZwkDC3doAouRpuMeBeWxUzyay5NAbAHrPY5gKdVa8Q2rJpJifVN36uzQ";
    static final int DEFAULT_TOKEN_CLAIM_USER_ID = 1;

    @Bean
    @Qualifier("ids")
    public Queue<Integer> ids() {
        return new ConcurrentLinkedQueue<>();
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder(Queue<Integer> ids) {
        return token -> {
            var id = ids.poll();
            return Jwt
                .withTokenValue(token)
                .header("Authorization", "Bearer " + token)
                .claim("id", id == null ? DEFAULT_TOKEN_CLAIM_USER_ID : id)
                .build();
        };
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
