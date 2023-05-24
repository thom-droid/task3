package com.sharetreats.department;

import com.sharetreats.exception.CustomRuntimeException;
import com.sharetreats.exception.CustomRuntimeExceptionCode;
import com.sharetreats.test_utils.DepartmentRepositoryTestImpl;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DepartmentControllerTest {

    DepartmentRepositoryTestImpl departmentRepository = new DepartmentRepositoryTestImpl();
    DepartmentService departmentService = new DepartmentServiceImpl(departmentRepository);
    DepartmentController departmentController = new DepartmentController(departmentService);

    @Test
    void givenMalformedCommand_thenThrows() {
        String input1 = "DEV , DEV";
        String input2 = "Dev 12,";
        String input3 = "DEV ,, DEV";

        String input4 = "DEV>>> FE";
        String input5 = "DEV>>FE";

        Throwable t1 = assertThrows(CustomRuntimeException.class, () -> departmentController.parseCommand(input1));
        Throwable t2 = assertThrows(CustomRuntimeException.class, () -> departmentController.parseCommand(input2));
        Throwable t3 = assertThrows(CustomRuntimeException.class, () -> departmentController.parseCommand(input3));
        Throwable t4 = assertThrows(CustomRuntimeException.class, () -> departmentController.parseCommand(input4));
        Throwable t5 = assertThrows(CustomRuntimeException.class, () -> departmentController.parseCommand(input5));

        List<Throwable> results = List.of(t1, t2, t3, t4, t5);

        for (Throwable result : results) {
            assertEquals(CustomRuntimeExceptionCode.NOT_VALID_COMMAND.getMessage(), result.getMessage());
        }
    }
}