package io.mipt.typeeleven.service.core.exception;

public class TypeElevenMessengerException extends RuntimeException {
    public TypeElevenMessengerException(String message) {
        super(message);
    }
    public TypeElevenMessengerException(String message, Throwable cause) {
        super(message, cause);
    }
}
