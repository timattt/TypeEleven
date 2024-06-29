package io.mipt.typeeleven.web.grpc;

import io.grpc.StatusRuntimeException;
import io.mipt.typeeleven.grpc.EmptyRequest;
import io.mipt.typeeleven.grpc.MessengerGrpc;
import io.mipt.typeeleven.service.core.ChattedMessengerService;
import io.mipt.typeeleven.web.grpc.mapper.GrpcMapper;
import io.mipt.typesix.businesslogic.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost",
        "grpc.port=9090",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.liquibase.enabled=false",
        "spring.profiles.active=test",
        // H2 DATA BASE
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=tmp_user",
        "spring.datasource.password=tmp_password",
})
public class MessengerGrpcAdapterTests {
    @Autowired
    @Qualifier("securedClient")
    private MessengerGrpc.MessengerBlockingStub securedClient;
    @Autowired
    @Qualifier("unsecuredClient")
    private MessengerGrpc.MessengerBlockingStub unsecuredClient;
    @Autowired
    private GrpcMapper grpcMapper;
    @MockBean
    private ChattedMessengerService chattedMessengerService;

    @Test
    public void listUsersSuccessTest() {
        User user = User.builder().id(1).email("a@gmail.com").build();
        when(chattedMessengerService.listAvailableUsers()).thenReturn(Flux.just(user));

        var users = securedClient.listUsers(EmptyRequest.newBuilder().build()).getUsersList();

        verify(chattedMessengerService).listAvailableUsers();

        Assertions.assertEquals(List.of(grpcMapper.toGrpcUser(user)), users);
    }

    @Test
    public void listUsersUnauthorizedTest() {
        when(chattedMessengerService.listAvailableUsers()).thenReturn(Flux.just());

        Assertions.assertThrows(StatusRuntimeException.class, () -> unsecuredClient.listUsers(EmptyRequest.newBuilder().build()));

        verify(chattedMessengerService, never()).listAvailableUsers();
    }

}
