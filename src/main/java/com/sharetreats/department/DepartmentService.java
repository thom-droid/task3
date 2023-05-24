package com.sharetreats.department;

public interface DepartmentService {

    String getDepartment(String name);

    String post(Department department);

    void delete(String departmentName);

    String update(Department department);

    String relate(String superior, String subordinate);

}
