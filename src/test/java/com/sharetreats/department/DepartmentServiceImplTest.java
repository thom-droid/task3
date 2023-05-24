package com.sharetreats.department;

import com.sharetreats.exception.CustomRuntimeException;
import com.sharetreats.exception.CustomRuntimeExceptionCode;
import com.sharetreats.test_utils.DepartmentRepositoryTestImpl;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DepartmentServiceImplTest {

    DepartmentRepositoryTestImpl departmentRepository = new DepartmentRepositoryTestImpl();
    DepartmentService departmentService = new DepartmentServiceImpl(departmentRepository);

    @Test
    void givenNewDepartmentWithDuplicatedName_whenPost_thenThrows() {

        //given
        String duplicatedName = departmentRepository.getRandomName();
        Department department = Department.of(10, duplicatedName);

        //then
        Throwable t = assertThrows(
                CustomRuntimeException.class, () -> departmentService.post(department));
        assertEquals(CustomRuntimeExceptionCode.DUPLICATED_NAME.getMessage(), t.getMessage());

    }

    @Test
    void givenDepartmentNameNotMatchedInStorage_whenGet_thenThrows() {

        //given
        String removedName = departmentRepository.getRemovedName();

        //when and then

        Throwable t = assertThrows(
                CustomRuntimeException.class, () -> departmentService.getDepartment(removedName));
        assertEquals(CustomRuntimeExceptionCode.NO_SUCH_DEPARTMENT.getMessage(), t.getMessage());
        //Todo when removed, cache must be updated too
    }

    @Test
    void givenRootDepartment_whenDelete_thenThrows() {

        //given
        String randomRootName = departmentRepository.getRandomRootDepartment();

        //when and then
        Throwable t = assertThrows(CustomRuntimeException.class, () -> departmentService.delete(randomRootName));
        assertEquals(CustomRuntimeExceptionCode.ROOT_CANNOT_BE_DELETE.getMessage(), t.getMessage());

    }

    @Test
    void givenTwoExistingDepartments_whenRelatedWithRootSet_thenSucceed() {

        //given
        Department root = departmentRepository.save(Department.of(5, "TESTROOT", true));
        Department superior = departmentRepository.save(Department.of(10, "SUP"));
        Department subordinate = departmentRepository.save(Department.of(5, "SUB"));
        root.add(superior);

        //when
        String result = assertDoesNotThrow(() -> departmentService.relate(superior.getName(), subordinate.getName()));

        System.out.println(result);
    }

    @Test
    void givenTwoExistingDepartments_whenRelatedWithoutRoot_thenSucceed() {

        //given
        Department superior = departmentRepository.save(Department.of(10, "SUP"));
        Department subordinate = departmentRepository.save(Department.of(5, "SUB"));

        //when
        assertDoesNotThrow(() -> departmentService.relate(superior.getName(), subordinate.getName()));

    }

    @Test
    void givenDepartmentThatHasSubordinates_whenSetAsRoot_thenAlsoRootOfSubordinatesSet() {

        //given
        Department a = departmentRepository.save(Department.of(10, "A"));
        Department b = departmentRepository.save(Department.of(15, "B"));
        Department c = departmentRepository.save(Department.of(7, "C"));

        //b>c
        departmentService.relate(b.getName(), c.getName());

        //a>b
        departmentService.relate(a.getName(), b.getName());

        //when *>a,
        departmentService.relate("*", a.getName());

        //then
        assertEquals(a, b.getRoot());
        assertEquals(a, c.getRoot());

    }

    @Test
    void givenDepartmentThatHasRoot_whenSetAsRoot_thenThrows() {
        //given
        Department a = departmentRepository.save(Department.of(10, "A"));
        Department b = departmentRepository.save(Department.of(15, "B"));
        Department c = departmentRepository.save(Department.of(7, "C"));

        //b>c
        departmentService.relate(b.getName(), c.getName());

        //a>b
        departmentService.relate(a.getName(), b.getName());

        //when *>a,
        departmentService.relate("*", a.getName());

        //then
        Throwable t = assertThrows(
                CustomRuntimeException.class, () -> departmentService.relate("*", b.getName()));
        Throwable t2 = assertThrows(
                CustomRuntimeException.class, () -> departmentService.relate("*", c.getName()));

        assertEquals(CustomRuntimeExceptionCode.ROOT_IS_ALREADY_SET.getMessage(), t.getMessage());
        assertEquals(CustomRuntimeExceptionCode.ROOT_IS_ALREADY_SET.getMessage(), t2.getMessage());
    }

    @Test
    void givenDepartmentWithNewHeadCount_whenUpdate_thenSucceeds() {

        //given
        Department a = departmentRepository.save(Department.of(10, "A", true));
        Department b = departmentRepository.save(Department.of(5, "B"));
        Department c = departmentRepository.save(Department.of(8, "C"));
        Department d = departmentRepository.save(Department.of(12, "D"));

        //relation *>A>B>C>D, total headcount 35
        departmentService.relate("*", a.getName());
        departmentService.relate(a.getName(), b.getName());
        departmentService.relate(b.getName(), c.getName());
        departmentService.relate(c.getName(), d.getName());

        int expectedHeadCount = 15;
        int expectedTotalHeadCount = 38;

        //when
        Department updated = Department.of(15, "D");

        departmentService.update(updated);

        int actualHeadcount = d.getHeadCount();

        List<Integer> actualTotalHeadCounts = List.of(
                a.getTotalHeadCountOfDepartment(),
                b.getTotalHeadCountOfDepartment(),
                c.getTotalHeadCountOfDepartment(),
                d.getTotalHeadCountOfDepartment());

        //then
        assertEquals(expectedHeadCount, actualHeadcount);
        for (Integer actual : actualTotalHeadCounts) {
            assertEquals(expectedTotalHeadCount, actual);
        }
    }
}