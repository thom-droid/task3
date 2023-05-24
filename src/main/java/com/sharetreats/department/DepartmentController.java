package com.sharetreats.department;

import com.sharetreats.command.CommandRegex;
import com.sharetreats.exception.CustomNumberFormatException;
import com.sharetreats.exception.CustomRuntimeException;
import com.sharetreats.exception.CustomRuntimeExceptionCode;

import java.util.Arrays;

public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public String parseCommand(String input) {

        if (!(CommandRegex.COMMAND.matches(input))) {
            throw new CustomRuntimeException(CustomRuntimeExceptionCode.NOT_VALID_COMMAND);
        }

        if (CommandRegex.COMMA.matches(input)) {

            String[] segments = segment(input, ",");

            Department department = toDepartmentFrom(segments);

            return departmentService.post(department);

        } else if (CommandRegex.RELATION.matches(input)) {

            String[] segments = segment(input, ">");

            validateLength(segments);

            String sup = validateName(segments[0]);
            String sub = validateName(segments[1]);

            return departmentService.relate(sup, sub);

        } else if (CommandRegex.UPDATE.matches(input)) {

            String[] segments = segment(input, "@");

            Department department = toDepartmentFrom(segments);

            return departmentService.update(department);

        } else if (CommandRegex.UPPERCASE.matches(input)) {

            return departmentService.getDepartment(input);
        }

        throw new CustomRuntimeException(CustomRuntimeExceptionCode.NOT_VALID_COMMAND);
    }

    private String[] segment(String input, String delimiter) {
        return Arrays.stream(input.trim().split(delimiter)).map(String::trim).toArray(String[]::new);
    }

    private void validateLength(String[] segments) {
        if (segments.length != 2)
            throw new CustomRuntimeException(CustomRuntimeExceptionCode.NOT_VALID_COMMAND);
    }

    private String validateName(String name) {
        if(name.equals("*")) return name;
        if (!CommandRegex.UPPERCASE.matches(name))
            throw new CustomRuntimeException(CustomRuntimeExceptionCode.NOT_VALID_NAME);
        return name;
    }

    private int validateHeadCount(String headCount) {
        try {
            return Integer.parseInt(headCount);
        } catch (IllegalArgumentException e) {
            throw new CustomRuntimeException(new CustomNumberFormatException(), CustomRuntimeExceptionCode.NOT_VALID_COMMAND);
        }
    }

    private Department toDepartmentFrom(String[] segments) {
        validateLength(segments);
        String name = validateName(segments[0]);
        int headcount = validateHeadCount(segments[1]);
        return Department.of(headcount, name);
    }
}
