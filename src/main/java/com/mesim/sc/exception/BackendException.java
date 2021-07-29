package com.mesim.sc.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BackendException extends Exception {

    // TODO 오류코드 정의 필요
    String errorCode;

    public BackendException(String message) {
        super(message);
    }

    public BackendException(String head, Throwable cause) {
        super(head + ", " + cause.getMessage(), cause);
    }

    public BackendException(String errorCode, String head, Throwable cause) {
        super(head + ", " + cause.getMessage(), cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode == null ? super.getMessage() : "[" + this.errorCode + "] " + super.getMessage();
    }
}
