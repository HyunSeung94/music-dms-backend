package com.mesim.sc.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BackendRuntimeException extends RuntimeException {

    // TODO 오류코드 정의 필요
    String errorCode;

    public BackendRuntimeException(String message) {
        super(message);
    }

    public BackendRuntimeException(Throwable cause) {
        super(cause.getMessage(), cause.getCause());
    }

    public BackendRuntimeException(String errorCode, Throwable cause) {
        super(cause.getMessage(), cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode == null ? super.getMessage() : "[" + this.errorCode + "] " + super.getMessage();
    }
}
