package io.mipt.typeeleven.web.grpc;

import io.grpc.StatusRuntimeException;
import io.mipt.typeeleven.BaseTest;
import io.mipt.typeeleven.grpc.EmptyRequest;
import io.mipt.typeeleven.grpc.MessengerGrpc;
import io.mipt.typeeleven.service.core.TypeElevenMessengerService;
import io.mipt.typeeleven.web.grpc.mapper.GrpcMapper;
import io.mipt.typesix.businesslogic.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.*;

public class MessengerGrpcAdapterTests extends BaseTest {
    @Autowired
    @Qualifier("securedClient")
    private MessengerGrpc.MessengerBlockingStub securedClient;
    @Autowired
    @Qualifier("unsecuredClient")
    private MessengerGrpc.MessengerBlockingStub unsecuredClient;
    @Autowired
    private GrpcMapper grpcMapper;
    @MockBean
    private TypeElevenMessengerService typeElevenMessengerService;

    @Test
    public void listUsersSuccessTest() {
        User user = User.builder().id(1).email("a@gmail.com").build();
        when(typeElevenMessengerService.listAvailableUsers()).thenReturn(Flux.just(user));

        var users = securedClient.listUsers(EmptyRequest.newBuilder().build()).getUsersList();

        verify(typeElevenMessengerService).listAvailableUsers();

        Assertions.assertEquals(List.of(grpcMapper.toGrpcUser(user)), users);
    }

    @Test
    public void listUsersUnauthorizedTest() {
        when(typeElevenMessengerService.listAvailableUsers()).thenReturn(Flux.just());

        Assertions.assertThrows(StatusRuntimeException.class, () -> unsecuredClient.listUsers(EmptyRequest.newBuilder().build()));

        verify(typeElevenMessengerService, never()).listAvailableUsers();
    }

}
