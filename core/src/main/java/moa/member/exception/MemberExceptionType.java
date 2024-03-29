package moa.member.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import moa.global.exception.MoaExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements MoaExceptionType {

    NOT_FOUND_MEMBER(NOT_FOUND, "회원을 찾을 수 없습니다."),
    ALREADY_EXISTS_EMAIL(CONFLICT, "해당 이메일로 이미 가입된 계정이 있습니다."),
    ALREADY_EXISTS_PHONE(CONFLICT, "해당 전화번호로 이미 가입된 계정이 있습니다."),
    NOT_VERIFIED_PHONE(FORBIDDEN, "핸드폰 검증이 이루어지지 않았습니다."),
    FAILED_SEND_PHONE_VERIFICATION_NUMBER(INTERNAL_SERVER_ERROR, "핸드폰 인증번호 발송에 실패했습니다."),
    DIFFERENT_PHONE_VERIFICATION_NUMBER(UNAUTHORIZED, "핸드폰 인증번호가 일치하지 않습니다."),
    ALREADY_SIGNED_UP(BAD_REQUEST, "이미 회원가입한 회원입니다."),
    PHONE_VERIFICATION_NOT_SENT(BAD_REQUEST, "핸드폰 인증번호가 전송되지 않았거나 만료되었습니다."),
    ALREADY_WITHDRAW(BAD_REQUEST, "이미 탈퇴한 회원입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private String detailMessage;

    MemberExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public MemberExceptionType withDetail(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        if (detailMessage == null) {
            return message;
        }
        return MESSAGE_FORMAT.formatted(message, detailMessage).strip();
    }
}
