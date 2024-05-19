package org.shlimtech.typeeleven.web.grpc.mapper;

import org.shlimtech.typeeleven.domain.model.Chat;
import org.shlimtech.typeeleven.domain.model.Message;
import org.shlimtech.typeeleven.grpc.Type11Chat;
import org.shlimtech.typeeleven.grpc.Type11Message;
import org.shlimtech.typeeleven.grpc.Type6User;
import org.shlimtech.typesixbusinesslogic.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GrpcMapper {
    public Type11Message toGrpcMessage(Message message) {
        return Type11Message.newBuilder()
                .setContent(message.getContent())
                .setId(message.getId())
                .setChatId(message.getChat().getId())
                .setTime(message.getTime())
                .setSenderId(message.getSenderId())
                .build();
    }

    public Type6User toGrpcUser(User user) {
        return Type6User.newBuilder()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .build();
    }

    public Type11Chat toGrpcChat(Chat chat) {
        return Type11Chat.newBuilder()
                .addAllMessages(chat.getMessages() == null ? Collections.emptyList() : chat.getMessages().stream().map(this::toGrpcMessage).toList())
                .setId(chat.getId())
                .addAllUsers(chat.getActiveUsers())
                .build();
    }
}
