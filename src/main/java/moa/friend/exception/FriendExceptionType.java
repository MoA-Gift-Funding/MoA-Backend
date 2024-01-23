package moa.friend.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum FriendExceptionType implements MoaExceptionType {

    NOT_FOUND_FRIEND(NOT_FOUND, "친구를 찾을 수 없습니다."),
    NO_AUTHORITY(FORBIDDEN, "해당 친구에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    FriendExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
