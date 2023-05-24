package com.sharetreats.exception;

public enum CustomRuntimeExceptionCode {

    DUPLICATED_NAME("중복된 부서 이름입니다. 다른 부서 이름을 설정해주세요."),
    ROOT_CANNOT_BE_SUBORDINATED("최상위 부서는 다른 부서의 하위 부서가 될 수 없습니다."),
    ROOT_CANNOT_BE_DELETE("최상위 부서는 삭제할 수 없습니다."),
    ROOT_IS_ALREADY_SET("이미 최상위 부서가 있는 부서입니다. "),
    NO_SUPERIOR_IS_SET("상위 부서 또는 최상위 부서가 설정되어 있지 않습니다"),
    NO_SUCH_DEPARTMENT("존재하지 않는 부서입니다. 부서명을 확인해주세요."),

    NOT_VALID_NAME("부서 이름은 영어 대문자만 입력가능합니다."),
    NOT_VALID_HEADCOUNT("부서 인원은 0 이상 1000이하의 정수여야 합니다."),

    NOT_VALID_COMMAND("명령어 입력이 잘못되었습니다. 매뉴얼을 확인해주세요."),
    ;

    final String message;

    CustomRuntimeExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
