package moa.global.exception;

import org.springframework.http.HttpStatus;

public interface MoaExceptionType {

    String MESSAGE_FORMAT = "%s \n  -> detail: []";

    MoaExceptionType withDetail(String detailMessage);

    HttpStatus getHttpStatus();

    String getMessage();

    String name();
}
