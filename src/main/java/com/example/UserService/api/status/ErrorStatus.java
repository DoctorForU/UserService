package com.example.UserService.api.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorStatus implements BaseCode {
    //common
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON404","Unknown error occured"),

    //user
    _USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "해당 사용자가 존재하지 않습니다."),
    _USER_NOT_FOUND_BY_EMAIL(HttpStatus.BAD_REQUEST, "USER4002", "해당 이메일의 사용자가 존재하지 않습니다."),
    _ALREADY_JOINED_USER(HttpStatus.BAD_REQUEST, "USER4003", "해당 아이디로 이미 가입한 유저가 존재합니다."),
    _INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER4004", "잘못된 비밀번호입니다."),

    //mail
    _VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL_VERIFICATION4001", "만료된 인증 코드입니다."),
    _VERIFICATION_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMAIL_VERIFICATION4002", "존재하지 않는 인증 코드입니다."),

    //token
    _TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN_VERIFICATION4001", "존재하지 않는 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}