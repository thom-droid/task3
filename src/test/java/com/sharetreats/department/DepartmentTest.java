package com.sharetreats.department;

import com.sharetreats.exception.CustomRuntimeException;
import com.sharetreats.exception.CustomRuntimeExceptionCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DepartmentTest {

    Department root = Department.of(1, "ROOT", true);
    Department a = Department.of(2, "A");
    Department b = Department.of(3, "B");
    Department c = Department.of(4, "C");
    Department d = Department.of(5, "D");

    //expected hierarchy
    // root(1) - a(2) - b (3) - c (4)
    //         - d(5)

    @BeforeEach
    void setup() {
        root.add(a);
        root.add(d);
        a.add(b);
        b.add(c);
    }

    @Test
    public void givenDepartments_whenAddingNewRelation_thenCombinedHeadCountOfRootSumsUp() {

        int expected = 15;
        int actual = root.getCombinedHeadCount();

        assertEquals(expected, actual);

    }

    @Test
    public void givenDepartments_whenAddingNewRelation_thenWellRelated() {

        List<Department> roots = List.of(a.getRoot(), b.getRoot(), c.getRoot(), d.getRoot());

        for (Department r : roots) {
            assertEquals(root, r);
        }
    }

    @Test
    public void givenAnyDepartment_whenTryingToGetHeadCount_thenRootHeadCountIsReturned() {

        int expected = 15;

        List<Integer> results = List.of(
                a.getTotalHeadCountOfDepartment(),
                b.getTotalHeadCountOfDepartment(),
                c.getTotalHeadCountOfDepartment(),
                d.getTotalHeadCountOfDepartment()
        );

        for (Integer result : results) {
            assertEquals(expected, result);
        }
    }

    @Test
    public void givenNewHierarchy_whenADepartmentRelocatedFromOther_thenUpdateHeadCount() {

        Department newRoot = Department.of(10, "NEWROOT", true);
        Department e = Department.of(6, "E");
        Department f = Department.of(7, "F");

        // newRoot, e,f 로 구성된 새로운 부서 조직, 10 + 6(e) + 7(f) = 23 명
        newRoot.add(e);
        e.add(f);

        // when
        // 7명인 f를 root 부서로 이동
        root.add(f);

        // 7명이 추가되므로 root의 총원은 22명
        int expectedHeadCountOfRoot = 22;

        List<Integer> results = List.of(
                root.getTotalHeadCountOfDepartment(),
                a.getTotalHeadCountOfDepartment(),
                b.getTotalHeadCountOfDepartment(),
                c.getTotalHeadCountOfDepartment(),
                d.getTotalHeadCountOfDepartment(),
                f.getTotalHeadCountOfDepartment()
        );

        for (Integer result : results) {
            assertEquals(expectedHeadCountOfRoot, result);
        }

        // 7명이 빠진 부서는 23 - 7 = 16명
        int expectedHeadCountOfNewRoot = 16;

        assertEquals(expectedHeadCountOfNewRoot, newRoot.getTotalHeadCountOfDepartment());
    }

    @Test
    public void givenNewDepartment_whenNameIsNotUppercaseAlphabet_thenThrows() {
        Throwable t = assertThrows(CustomRuntimeException.class, () -> Department.of(15, "abC"));
        assertEquals(CustomRuntimeExceptionCode.NOT_VALID_NAME.getMessage(), t.getMessage());
    }

    @Test
    public void givenNewDepartment_whenHeadCountIsNotBetween0And1000_thenThrows() {
        Throwable t = assertThrows(CustomRuntimeException.class, () -> Department.of(-15, "DEV"));
        assertEquals(CustomRuntimeExceptionCode.NOT_VALID_HEADCOUNT.getMessage(), t.getMessage());
        Throwable t2 = assertThrows(CustomRuntimeException.class, () -> Department.of(1010, "DEV"));
        assertEquals(CustomRuntimeExceptionCode.NOT_VALID_HEADCOUNT.getMessage(), t2.getMessage());
    }

    @Test
    public void givenNewDepartment_whenTryingToSubordinateRoot_thenThrows() {
        Department e = Department.of(10, "E");
        Throwable t = assertThrows(CustomRuntimeException.class, () -> e.add(root));
        assertEquals(CustomRuntimeExceptionCode.ROOT_CANNOT_BE_SUBORDINATED.getMessage(), t.getMessage());
    }

    @Test
    void givenDepartmentAndNewHeadCount_whenUpdateCache_thenSucceed() {

        //given
        int changeOfHeadcount = 10;
        int expectedHeadCountOfRoot = 24;

        //when
        // a has 1, updating to 10, so combined headcount will be 15 + 10 - 1 = 24
        root.updateHeadcount(changeOfHeadcount);

        List<Integer> results1 = List.of(
                a.getTotalHeadCountOfDepartment(),
                b.getTotalHeadCountOfDepartment(),
                c.getTotalHeadCountOfDepartment(),
                d.getTotalHeadCountOfDepartment()
        );

        //then
        for (Integer result : results1) {
            assertEquals(expectedHeadCountOfRoot, result);
        }

        //given
        int expectedHeadCountOfRoot2 = 30;

        // 현재 b와 그 하위부서 c의 합은 3 + 4 = 7, c가 6명 증가하므로 b는 13으로 증가.
        // 마찬가지로 a의 경우 9명에서 15명으로 증가
        int expectedHeadCountOfB = 13;
        int expectedHeadCountOfA = 15;

        //when
        // c 의 인원수가 4명에서 10명으로 6명 늘어남에 따라 이 부서를 포함하는 최상위 부서의 인원수도 변경됨
        // 또한 c의 상위 부서인 b와 a의 캐시(a 부서와 그 하위부서의 인원수, b 부서와 그 하위부서의 인원수)도 변경
        c.updateHeadcount(changeOfHeadcount);

        List<Integer> results2 = List.of(
                a.getTotalHeadCountOfDepartment(),
                b.getTotalHeadCountOfDepartment(),
                c.getTotalHeadCountOfDepartment(),
                d.getTotalHeadCountOfDepartment()
        );

        //then
        for (Integer result2 : results2) {
            assertEquals(expectedHeadCountOfRoot2, result2);
        }

        assertEquals(expectedHeadCountOfB, b.getCombinedHeadCount());
        assertEquals(expectedHeadCountOfA, a.getCombinedHeadCount());

    }

}
