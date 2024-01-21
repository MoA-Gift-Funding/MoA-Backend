package moa.global.exception;

import org.springframework.http.HttpStatus;

public interface MoaExceptionType {

    HttpStatus getHttpStatus();

    String getMessage();
}
