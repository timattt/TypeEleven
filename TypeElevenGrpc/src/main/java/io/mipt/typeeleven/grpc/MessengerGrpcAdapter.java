package io.mipt.typeeleven.grpc;

import io.mipt.typeeleven.core.service.api.TypeElevenMessengerService;
import io.mipt.typeeleven.grpc.mapper.GrpcMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;

@Log
@GRpcService
@RequiredArgsConstructor
public class MessengerGrpcAdapter extends ReactorMessengerGrpc.MessengerImplBase {
    private final GrpcMapper grpcMapper;
    private final TypeElevenMessengerService messengerService;
    private final Sinks.Many<ExchangeResponse> sink = Sinks.many().multicast().directBestEffort();

    @Override
    @Secured({})
    public Mono<ListUsersResponse> listUsers(Mono<EmptyRequest> request) {
        return messengerService.listAvailableUsers()
                .map(grpcMapper::toGrpcUser).collectList()
                .map(list -> ListUsersResponse.newBuilder().addAllUsers(list).build());
    }

    @Override
    @Secured({})
    public Mono<ListChatsResponse> listChats(Mono<EmptyRequest> request) {
        return getUserIdReactor()
                .flatMap(id -> messengerService.listChats(id).map(grpcMapper::toGrpcChat).collectList())
                .map(list -> ListChatsResponse.newBuilder().addAllChats(list).build());
    }

    @Override
    @Secured({})
    public Mono<ListMessagesResponse> listMessages(Mono<ListMessagesRequest> request) {
        return request.flatMap(requestData -> messengerService
                .listMessages(requestData.getChatId(), requestData.getFromTime(), requestData.getCount())
                .map(grpcMapper::toGrpcMessage).collectList())
                .map(list -> ListMessagesResponse.newBuilder().addAllMessages(list).build());
    }

    @Override
    @Secured({})
    public Mono<NewChatResponse> newChat(Mono<NewChatRequest> request) {
        return getUserIdReactor().flatMap(userId -> request
                .flatMap(requestData -> messengerService
                        .createChat(List.of(userId, requestData.getReceiverId())))
                .map(grpcMapper::toGrpcChat))
                .map(chat -> NewChatResponse.newBuilder().setChat(chat).build());
    }

    @Override
    @Secured({})
    public Flux<ExchangeResponse> receiveMessages(Mono<EmptyRequest> request) {
        return getUserIdReactor()
                .flatMapMany(userId -> sink
                        .asFlux()
                        .filterWhen(exchangeResponse -> messengerService
                                .listActiveUsersForMessageChat(exchangeResponse.getMessage().getId())
                                .any(activeId -> activeId.equals(userId))
                        )
                        .filter(exchangeResponse -> exchangeResponse.getMessage().getSenderId() != userId)
                );
    }

    @Override
    @Secured({})
    public Mono<SendMessageResponse> sendMessage(Mono<SendMessageRequest> request) {
        return getUserIdReactor().flatMap(userId -> request
                .flatMap(requestData -> messengerService
                        .newMessage(userId, requestData.getChatId(), requestData.getContent())
                        .flatMap(message -> Mono
                                .just(grpcMapper.toGrpcMessage(message))
                                .doOnNext(type11Message -> sink
                                        .tryEmitNext(ExchangeResponse.newBuilder().setMessage(type11Message).build())
                        )
                ))
                .map(message -> SendMessageResponse.newBuilder().setMessage(message).build()));
    }

    private Mono<Integer> getUserIdReactor() {
        return Mono.just(GrpcSecurity.AUTHENTICATION_CONTEXT_KEY.get())
                .cast(JwtAuthenticationToken.class)
                .map(auth -> auth.getToken().getClaims())
                .map(claims -> Integer.parseInt(claims.get("id").toString()))
                .onErrorMap(error -> new AuthenticationServiceException("Error while parsing token", error));
    }
}
