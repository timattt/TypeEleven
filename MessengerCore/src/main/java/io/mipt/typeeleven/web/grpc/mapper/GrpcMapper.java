package io.mipt.typeeleven.web.grpc.mapper;

import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.domain.model.Message;
import io.mipt.typeeleven.grpc.Type11Chat;
import io.mipt.typeeleven.grpc.Type11Message;
import io.mipt.typeeleven.grpc.Type6User;
import io.mipt.typesix.businesslogic.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class GrpcMapper {
    public Type11Message toGrpcMessage(Message message) {
        return Type11Message.newBuilder()
                .setContent(message.getContent())
                .setId(message.getId())
                .setChatId(message.getChatId())
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
                .setId(chat.getId())
                .addAllUsers(chat.getActiveUsers())
                .build();
    }
}
