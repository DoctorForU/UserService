package com.example.UserService.api.status;

import org.springframework.http.HttpStatus;

public enum SuccessStatus implements BaseCode{

    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _ACCEPTED(HttpStatus.ACCEPTED, "COMMON204", "별도의 응답 데이터가 없으며, 정상 처리되었습니다.");

    private final HttpStatus httpStatus;
    private final String code; // 오류 상태
    private final String message; // 오류 설명

    SuccessStatus(HttpStatus httpStatus, String code, String message) {
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
}