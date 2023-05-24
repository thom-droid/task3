package com.sharetreats.command;

import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 입력받은 입력값을 검사하기 위한 정규식을 Pattern 클래스로 인스턴스화하여 저장하고 있는
 * 열거 타입입니다. {@code String.matches()} 를 사용하는 경우 정규식을 매번 컴파일하여
 * Pattern 인스턴스를 만들게 되므로 최초 한 번만 생성하고 애플리케이션 전체에서 사용할 수 있도록
 * 열거 타입으로 선언했습니다.
 * */
public enum CommandRegex {

    COMMAND(() -> Pattern.compile("^[A-Z>,*\\s\\d]+$")),
    COMMA(() -> Pattern.compile("^(?:[^,]*,){1}[^,]*$")),
    RELATION(() -> Pattern.compile("^(?:[^>]*>){1}[^>]*$")),
    UPDATE(() -> Pattern.compile("^(?:[^@]*>){1}[^@]*$")),
    UPPERCASE(() -> Pattern.compile("[A-Z]+")),

    ;
    final Pattern pattern;

    CommandRegex(Supplier<Pattern> supplier) {
        this.pattern = supplier.get();
    }

    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }
}
