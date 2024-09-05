package io.mipt.typeeleven.grpc.exception;

import io.grpc.Status;
import io.mipt.typeeleven.core.service.api.exception.TypeElevenMessengerException;
import lombok.extern.java.Log;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;
import org.springframework.security.core.AuthenticationException;

@Log
@GRpcServiceAdvice
public class GrpcAdvisor {
    @GRpcExceptionHandler
    public Status handleAuth(AuthenticationException exc, GRpcExceptionScope scope) {
        return Status.UNAUTHENTICATED.withDescription(exc.getMessage());
    }
    @GRpcExceptionHandler
    public Status handleService(TypeElevenMessengerException exc, GRpcExceptionScope scope) {
        return Status.INVALID_ARGUMENT.withDescription(exc.getMessage());
    }
    @GRpcExceptionHandler
    public Status handleException(Exception exc, GRpcExceptionScope scope) {
        exc.printStackTrace();
        return Status.INTERNAL.withDescription(exc.getMessage());
    }
}
