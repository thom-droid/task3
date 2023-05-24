package com.sharetreats.test_utils;

import com.sharetreats.department.Department;
import com.sharetreats.department.DepartmentRepository;

import java.util.*;

public class DepartmentRepositoryTestImpl implements DepartmentRepository {

    private final Map<String, Department> storage;

    public DepartmentRepositoryTestImpl() {
        this.storage = new HashMap<>();
        setup();
    }

    @Override
    public Department save(Department department) {
        put(department);
        return department;
    }

    @Override
    public List<Department> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Department> findBy(String name) {
        return Optional.ofNullable(storage.get(name));
    }

    private void setup() {
        Department dev = Department.of(10, "DEV", true);
        dev.setAsRoot();
        Department backend = Department.of(20, "BACKEND", false);
        Department frontend = Department.of(20, "FRONTEND", false);
        Department devops = Department.of(30, "DEVOPS", false);
        dev.add(backend);
        dev.add(frontend);
        dev.add(devops);

        put(dev);
        put(backend);
        put(frontend);
        put(devops);
    }

    private void put(Department department) {
        storage.put(department.getName(), department);
    }

    public String getRandomName() {
        return storage.values().stream().findAny().get().getName();
    }

    public String getRemovedName() {
        String randomName = getRandomName();
        storage.remove(randomName);
        return randomName;
    }

    public String getRandomRootDepartment() {
        return storage.values().stream()
                .filter(Department::isThisRoot)
                .findAny()
                .orElse(Department.of(15, "RANDOMROOTZZ", true))
                .getName();
    }

}
