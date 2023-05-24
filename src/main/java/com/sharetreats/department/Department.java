package com.sharetreats.department;

import com.sharetreats.command.CommandRegex;
import com.sharetreats.exception.CustomRuntimeException;
import com.sharetreats.exception.CustomRuntimeExceptionCode;

import java.util.LinkedList;
import java.util.List;

/**
 * 부서를 표현하는 클래스입니다.
 * <p>
 *     각 부서는 여러 하위 부서를 가질 수 있도록 {@link List}로 표현하였고,
 *     상위부서는 하나만 가질 수 있도록 상위부서의 참조를 저장합니다.
 *     최상위 부서의 정보를 쉽게 불러올 수 있도록 최상위 부서가 있는 경우 최상위 부서의 참조를 가지도록 했습니다.
 * </p>
 * <p>
 *     부서 조직도에 변동이 생길 때 매번 변동된 모든 부서의 인원수를 다시 검색하지 않도록, {@code combinedHeadCount}
 *     를 통해 캐싱합니다. 하위 부서가 추가되거나 상위부서가 바뀌는 경우 재귀적으로 상하위 부서를 탐색하는데, 이 때
 *     이 캐시값이 있으면 바로 사용합니다.
 * </p>
 * <p>
 *     최상위 부서는 {@code isRoot}의 값이 {@code true}인 객체이며, 이 필드는 요구사항에 정의된 것처럼 다른 부서의 하위 부서가
 *     될 수 없도록 조건을 확인할 때 사용됩니다.
 * </p>
 * */

public class Department {

    private static final String MESSAGE_WITHOUT_ROOT = "최상위 부서가 설정되어 있지 않아 현재 부서의 상위 부서 중 최고 부서의 정보가 표시됩니다.";

    private int headCount;
    private int combinedHeadCount;
    private final String name;
    private final List<Department> subordinates;
    private Department superior;
    private Department root;
    private boolean isRoot;

    private Department(int headCount, String name, boolean isRoot) {
        this.headCount = headCount;
        this.name = name;
        this.subordinates = new LinkedList<>();
        this.isRoot = isRoot;
    }

    public static Department of(int headCount, String name) {
        validate(headCount, name);
        return new Department(headCount, name, false);
    }

    public static Department of(int headCount, String name, boolean isRoot) {
        validate(headCount, name);
        return new Department(headCount, name, isRoot);
    }

    public int getCombinedHeadCount() {
        return combinedHeadCount;
    }

    public String getName() {
        return name;
    }

    public int getHeadCount() {
        return headCount;
    }

    public List<Department> getSubordinates() {
        return subordinates;
    }

    public Department getRoot() {
        return root;
    }

    public void setAsRoot() {
        // 최상위부서가 이미 있는 경우
        if (this.root != null) {
            throw new CustomRuntimeException(CustomRuntimeExceptionCode.ROOT_IS_ALREADY_SET);
        }

        this.isRoot = true;

        // 하위 부서가 있는 부서인 경우, 하위 부서의 최상위부서를 현재부서로 설정
        if (!this.subordinates.isEmpty()) {
            for (Department subordinate : subordinates) {
                setRoot(subordinate, this);
            }
        }
        updateHeadcount();
    }

    /**
     * 하위부서를 추가합니다.
     * <p>
     *     하위 부서의 인원 수와 현재 부서의 인원수를 더하여 {@code combinedHeadCount} 변수에 캐싱합니다.
     *     하위 부서의 인원수에 캐싱된 값이 있다면 그것을 사용하고, 없는 경우 하위 부서의 모든 하위부서를 탐색하여
     *     인원수를 구합니다.
     * </p>
     * <p>
     *     현재 부서를 하위부서의 상위부서로 설정하고, 현재 부서에 최상위부서(root)가 설정되어 있다면
     *     이 최상위부서를 하위부서의 최상위부서로 설정합니다. 이 작업은 하위 부서의 모든 하위부서에 재귀적으로
     *     적용됩니다.
     * </p>
     * */
    public void add(Department subordinate) {
        // 추가하려는 부서가 null 이면 리턴
        if (subordinate == null) {
            return;
        }

        // 추가하려는 부서가 최상위 부서면 예외 처리
        if (subordinate.isThisRoot()) {
            throw new CustomRuntimeException(CustomRuntimeExceptionCode.ROOT_CANNOT_BE_SUBORDINATED);
        }

        // 이미 관계가 설정된 부서면 리턴
        if (alreadyRelatedTo(subordinate)) {
            return;
        }

        // 캐시 업데이트를 위해 현재 부서의 루트(최상위)를 찾음. 없는 경우 가장 상위의 부서를 리턴
        // 새로 추가하는 부서의 사람 수를 현재 부서에 더해 캐시를 업데이트. 루트가 존재하는 경우 루트도 하위 부서에 설정
        Department root = findRootOrHighest();
        updateSubordinatesAndCache(subordinate, root);
    }

    public void updateHeadcount(int headCount) {
        this.headCount = headCount;
        updateHeadcount();
    }

    public int getTotalHeadCountOfDepartment() {
        if (isThisRoot()) {
            return this.combinedHeadCount;
        }
        Department highest = findRootOrHighest();
        if (highest == null) {
            return this.combinedHeadCount;
        }
        return highest.combinedHeadCount;
    }

    public boolean isThisRoot() {
        return isRoot;
    }

    public void throwDuplicatedNameException() {
        throw new CustomRuntimeException(CustomRuntimeExceptionCode.DUPLICATED_NAME);
    }

    public String relationToString() {
        if (this.root == null && !isThisRoot()) {
            Department highest = findRootOrHighest();
            return MESSAGE_WITHOUT_ROOT + "\n" +
                    "현재부서: [ " + this.getName() + " ], " +
                    "상위부서: [ " + highest.getName()+ " ], " +
                    "총 인원: [ " + highest.combinedHeadCount +" ]";
        }

        if (isThisRoot()) {
            return "현재 조회된 부서가 최상위 부서입니다. " +
                    "현재부서: [ " + this.getName() + " ], " +
                    "총 인원: [ " + this.combinedHeadCount + " ]";
        }

        return "현재부서: [ " + this.getName() + " ], " +
                "최상위부서: [ " + root.getName() + " ], 총 인원: [ " + root.getCombinedHeadCount() + " ]";
    }

    public String toString() {
        return "현재부서: [ " + this.getName() + " ], 현재부서의 인원: [ " + this.headCount + " ] ";
    }

    private Department findRootOrHighest() {
        if (!isThisRoot()) {
            return findRootOrHighest(this, null);
        }
        return this;
    }

    private boolean alreadyRelatedTo(Department subordinate) {
        return this.subordinates.contains(subordinate) || subordinate.superior == this;
    }

    private void updateSubordinatesAndCache(Department subordinate, Department rootOrHighest) {

        updateCombinedHeadCountFrom(subordinate);
        updateRelationOf(subordinate);

        // 현재 부서의 최상위 부서가 있다면 추가하려는 하위 부서의 모든 하위 부서에도 최상위 부서를 설정하고 캐시 업데이트
        if(rootOrHighest != null && rootOrHighest.isThisRoot()) {
            setRoot(subordinate, rootOrHighest);
        }

        // 현재 부서의 최상위부서까지 부서 인원 수 업데이트
        if (this.superior != null) {
            Department sup = this.superior;
            sup.updateHeadcount();
        }
    }

    private void updateCombinedHeadCountFrom(Department subordinate) {
        // cache 설정
        if (this.combinedHeadCount == 0) {
            this.combinedHeadCount = headCount;
        }

        int subHeadcount = subordinate.calculateHeadCount();

        if (this.combinedHeadCount + subHeadcount > 1000) {
            throw new CustomRuntimeException(CustomRuntimeExceptionCode.NOT_VALID_HEADCOUNT);
        }

        this.combinedHeadCount += subHeadcount;
    }

    private void updateRelationOf(Department subordinate) {
        // 추가하려는 하위 부서가 최상위 루트를 가지고 있었다면 그 상위 부서로부터 제거하고, 그 상위 부서의 캐시 업데이트
        Department oldSup = subordinate.superior;
        if (oldSup != null && oldSup != this) {
            oldSup.remove(subordinate);
        }

        // 상위 부서 - 하위 부서 관계 설정
        relateTo(subordinate);
    }

    private void remove(Department subordinate) {
        if (this.getSubordinates().remove(subordinate)) {
            updateHeadcount();
        }
    }

    private void updateHeadcount() {
        int count = headCount;

        for (Department subordinate : subordinates) {
            count += subordinate.calculateHeadCount();
        }

        combinedHeadCount = count;
        Department sup = this.superior;

        if (sup != null) {
            sup.updateHeadcount();
        }
    }

    private int calculateHeadCount() {
        int count = headCount;

        // 캐시된 부서 총원이 있다면 바로 리턴
        if (combinedHeadCount != 0) return combinedHeadCount;

        List<Department> subordinates = getSubordinates();

        for (Department s : subordinates) count += s.calculateHeadCount();

        // 하위 부서가 다른 하위 부서를 가지는 경우를 탐색 시간을 줄이기 위해 캐싱
        combinedHeadCount = count;

        return count;
    }

    private void setRoot(Department subordinate, Department root) {
        if (subordinate.root == root) {
            return;
        }

        subordinate.root = root;

        for (Department sub : subordinate.getSubordinates()) {
            setRoot(sub, root);
        }
    }

    private void relateTo(Department subordinate) {
        this.subordinates.add(subordinate);
        subordinate.superior = this;
    }

    private Department findRootOrHighest(Department department, Department prev) {
        if (department == null)
            return prev;
        if (!department.isRoot)
            department = findRootOrHighest(department.superior, department);

        return department;
    }

    private static void validate(int headCount, String departmentName) {
        validate(departmentName);
        validate(headCount);
    }

    private static void validate(String departmentName) {
        if(departmentName.equals("*")) return ;
        if (!CommandRegex.UPPERCASE.matches(departmentName))
            throw new CustomRuntimeException(CustomRuntimeExceptionCode.NOT_VALID_NAME);
    }

    private static void validate(int headCount) {
        if (headCount < 0 || headCount > 1000)
            throw new CustomRuntimeException(CustomRuntimeExceptionCode.NOT_VALID_HEADCOUNT);
    }
}
