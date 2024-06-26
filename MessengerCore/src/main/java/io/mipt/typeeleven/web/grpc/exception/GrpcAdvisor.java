package io.mipt.typeeleven.web.grpc.exception;

import io.grpc.Status;
import lombok.extern.java.Log;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;
import io.mipt.typeeleven.service.core.exception.TypeElevenMessengerException;
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
