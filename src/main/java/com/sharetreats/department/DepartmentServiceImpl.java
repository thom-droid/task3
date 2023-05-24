package com.sharetreats.department;

import com.sharetreats.exception.CustomRuntimeException;
import com.sharetreats.exception.CustomRuntimeExceptionCode;

public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public String getDepartment(String name) {
        Department d = findBy(name);

        return d.relationToString();
    }

    @Override
    public String post(Department department) {
        String name = department.getName();
        departmentRepository.findBy(name).ifPresent(Department::throwDuplicatedNameException);

        return departmentRepository.save(department).toString();
    }

    @Override
    public void delete(String departmentName) {
        Department d = findBy(departmentName);
        if (d.isThisRoot()) throw new CustomRuntimeException(CustomRuntimeExceptionCode.ROOT_CANNOT_BE_DELETE);
    }

    @Override
    public String update(Department department) {
        Department d = findBy(department.getName());
        d.updateHeadcount(department.getHeadCount());
        return d.toString();
    }

    @Override
    public String relate(String superior, String subordinate) {

        Department sub = findBy(subordinate);

        if (superior.equals("*")) {
            sub.setAsRoot();
        } else {
            Department sup = findBy(superior);
            sup.add(sub);
        }
        return sub.relationToString();
    }

    private Department findBy(String name) {
        return departmentRepository.findBy(name)
                .orElseThrow(
                        () -> new CustomRuntimeException(CustomRuntimeExceptionCode.NO_SUCH_DEPARTMENT)
                );
    }
}
