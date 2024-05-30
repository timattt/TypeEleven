package org.shlimtech.typeeleven.web.grpc;

import io.grpc.Status;
import lombok.extern.java.Log;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;
import org.shlimtech.typeeleven.service.core.exception.MessengerException;
import org.springframework.security.core.AuthenticationException;

@Log
@GRpcServiceAdvice
public class GrpcAdvisor {
    @GRpcExceptionHandler
    public Status handleAuth(AuthenticationException exc, GRpcExceptionScope scope) {
        exc.printStackTrace();
        return Status.UNAUTHENTICATED.withDescription(exc.getMessage());
    }
    @GRpcExceptionHandler
    public Status handleService(MessengerException exc, GRpcExceptionScope scope) {
        exc.printStackTrace();
        return Status.INVALID_ARGUMENT.withDescription(exc.getMessage());
    }
}
