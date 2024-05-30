package org.shlimtech.typeeleven.web.grpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.shlimtech.typeeleven.grpc.*;
import org.shlimtech.typeeleven.service.core.ChattedMessengerService;
import org.shlimtech.typeeleven.web.grpc.mapper.GrpcMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log
@GRpcService
@RequiredArgsConstructor
public class MessengerGrpcAdapter extends ReactorMessengerGrpc.MessengerImplBase {
    private final GrpcMapper grpcMapper;
    private final ChattedMessengerService messengerService;
    private final ConcurrentMap<Integer, Sinks.Many<ExchangeResponse>> connections = new ConcurrentHashMap<>();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();
    private final ObjectMapper objectMapper;

    @Override
    @Secured({})
    public Mono<ListUsersResponse> listUsers(Mono<EmptyRequest> request) {
        return getUserIdReactor().flatMapMany(id -> messengerService.listAvailableUsers())
                .map(grpcMapper::toGrpcUser).collectList()
                .map(list -> ListUsersResponse.newBuilder().addAllUsers(list).build());
    }

    @Override
    @Secured({})
    public Mono<ListChatsResponse> listChats(Mono<EmptyRequest> request) {
        return getUserIdReactor()
                .flatMap(id -> messengerService.listChats(id).flatMap(chat -> messengerService.).collectList())
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
        return getUserIdReactor().flatMapMany(userId -> {
            Sinks.Many<ExchangeResponse> sink = Sinks.many().replay().latest();
            connections.put(userId, sink);
            return sink.asFlux();
        });
    }

    @Override
    @Secured({})
    public Mono<SendMessageResponse> sendMessage(Mono<SendMessageRequest> request) {
        return getUserIdReactor().flatMap(userId -> request
                .flatMap(requestData -> messengerService
                        .newMessage(userId, requestData.getChatId(), requestData.getContent())
                        .flatMap(message -> Mono
                                .just(grpcMapper.toGrpcMessage(message))
                                .doOnNext(type11Message -> message.getChat().getActiveUsers().forEach(activeUserId -> {
                                    if (activeUserId != userId) {
                                        var stream = connections.get(activeUserId);
                                        if (stream != null) {
                                            try {
                                                stream.tryEmitNext(ExchangeResponse.newBuilder().setMessage(type11Message).build());
                                            } catch (Exception e) {
                                                connections.remove(activeUserId);
                                            }
                                        }
                                    }
                                }))
                        )
                ))
                .map(message -> SendMessageResponse.newBuilder().setMessage(message).build());
    }

    private Mono<Integer> getUserIdReactor() {
        return Mono.just(GrpcSecurity.AUTHENTICATION_CONTEXT_KEY.get())
                .cast(JwtAuthenticationToken.class)
                .map(auth -> auth.getToken().getTokenValue())
                .map(token -> token.split("\\."))
                .map(chunks -> {
                    String header = new String(decoder.decode(chunks[0]));
                    String payload = new String(decoder.decode(chunks[1]));

                    try {
                        Map<String, String> headerMap = objectMapper.readValue(header, Map.class);
                        Map<String, String> payloadMap = objectMapper.readValue(payload, Map.class);

                        return Integer.parseInt(payloadMap.get("id"));
                    } catch (JsonProcessingException e) {
                        throw new AuthenticationCredentialsNotFoundException("Error parsing id from token", e);
                    }
                })
                .onErrorMap(error -> new AuthenticationServiceException("Error while parsing token", error));
    }
}
