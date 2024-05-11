package org.shlimtech.typeeleven.grpc.mapper;

import org.shlimtech.typeeleven.domain.model.Message;
import org.shlimtech.typeeleven.grpc.GrpcMessage;
import org.springframework.stereotype.Component;

@Component
public class GrpcMapper {
    public GrpcMessage toGrpcMessage(Message message) {
        return GrpcMessage.newBuilder()
                .setReceiverEmail(message.getReceiverEmail())
                .setSenderEmail(message.getSenderEmail())
                .setContent(message.getContent())
                .setId(message.getId())
                .setTime(message.getTime())
                .build();
    }
}
