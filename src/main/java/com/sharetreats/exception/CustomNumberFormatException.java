package com.sharetreats.exception;

public class CustomNumberFormatException extends NumberFormatException {

    public CustomNumberFormatException() {
        super("부서의 인원 수는 양의 정수만 입력 가능합니다.");
    }

}
