package org.shlimtech.typeeleven.web.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.lognet.springboot.grpc.GRpcService;
import org.shlimtech.typeeleven.grpc.*;
import org.shlimtech.typeeleven.web.grpc.mapper.GrpcMapper;
import org.shlimtech.typeeleven.service.core.SimpleMessengerService;
import org.springframework.security.access.annotation.Secured;

@Log
@GRpcService
@RequiredArgsConstructor
public class MessengerGrpcAdapter extends MessengerGrpc.MessengerImplBase {
    private final SimpleMessengerService simpleMessengerService;
    private final GrpcMapper grpcMapper;

    @Override
    @Secured({})
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        log.info(request.toString());
        var message = request.getMessage();
        simpleMessengerService.sendMessage(message.getSenderEmail(), message.getReceiverEmail(), message.getContent());
        responseObserver.onNext(SendMessageResponse.newBuilder().setStatus(0).build());
        responseObserver.onCompleted();
    }

    @Override
    @Secured({})
    public void getMessagesAfter(GetMessageAfterRequest request, StreamObserver<GetMessageAfterResponse> responseObserver) {
        log.info(request.toString());
        responseObserver.onNext(GetMessageAfterResponse.newBuilder()
                .addAllMessages(
                        simpleMessengerService.getMessagesAfter(
                                request.getTime(),
                                request.getUserEmail()
                        ).stream().map(grpcMapper::toGrpcMessage).toList()
                ).build());
        responseObserver.onCompleted();
    }

    @Override
    @Secured({})
    public void getMessagesBefore(GetMessageBeforeRequest request, StreamObserver<GetMessageBeforeResponse> responseObserver) {
        log.info(request.toString());
        responseObserver.onNext(GetMessageBeforeResponse.newBuilder()
                .addAllMessages(
                        simpleMessengerService.getMessagesBefore(
                                request.getTime(),
                                request.getUserEmail(),
                                request.getCount()
                        ).stream().map(grpcMapper::toGrpcMessage).toList()
                ).build());
        responseObserver.onCompleted();
    }
}
