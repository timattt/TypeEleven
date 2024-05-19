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
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        return Type6User.newBuilder()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setFirstName(firstName == null ? "" : firstName)
                .setLastName(lastName == null ? "" : lastName)
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
