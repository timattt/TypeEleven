package org.shlimtech.typeeleven.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import net.devh.boot.grpc.server.service.GrpcService;
import org.shlimtech.typeeleven.grpc.mapper.GrpcMapper;
import org.shlimtech.typeeleven.service.core.SimpleMessengerService;

@Log
@GrpcService
@RequiredArgsConstructor
public class MessengerGrpcAdapter extends MessengerGrpc.MessengerImplBase {
    private final SimpleMessengerService simpleMessengerService;
    private final GrpcMapper grpcMapper;

    @Override
    public void sendMessage(SendMessageRequest request, StreamObserver<SendMessageResponse> responseObserver) {
        log.info(request.toString());
        var message = request.getMessage();
        simpleMessengerService.sendMessage(message.getSenderEmail(), message.getReceiverEmail(), message.getContent());
        responseObserver.onNext(SendMessageResponse.newBuilder().setStatus(0).build());
        responseObserver.onCompleted();
    }

    @Override
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
