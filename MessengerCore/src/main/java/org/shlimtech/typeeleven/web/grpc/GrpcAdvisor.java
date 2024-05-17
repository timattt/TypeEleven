package org.shlimtech.typeeleven.web.grpc;

import io.grpc.Status;
import lombok.extern.java.Log;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;

@Log
@GRpcServiceAdvice
public class GrpcAdvisor {
    @GRpcExceptionHandler
    public Status handle(Exception exc, GRpcExceptionScope scope){
        return Status.INTERNAL.withDescription(exc.getLocalizedMessage());
    }
}
