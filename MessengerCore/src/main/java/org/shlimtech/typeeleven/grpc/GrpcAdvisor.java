package org.shlimtech.typeeleven.grpc;

import io.grpc.Status;
import lombok.extern.java.Log;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Log
@GrpcAdvice
public class GrpcAdvisor {
    @GrpcExceptionHandler
    public Status handle(Exception e) {
        log.severe(e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage()).withCause(e);
    }
}
