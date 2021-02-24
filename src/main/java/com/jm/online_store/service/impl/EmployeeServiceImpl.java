package com.jm.online_store.service.impl;

import com.jm.online_store.model.dto.EmployeeDto;
import com.jm.online_store.service.interf.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public List<EmployeeDto> findAllEmployee() {
        return null;
    }

    @Override
    public EmployeeDto findEmployeeById(Long id) {
        return null;
    }

    @Override
    public EmployeeDto updateEmployee(EmployeeDto employee) {
        return null;
    }

    @Override
    public EmployeeDto updateEmployeeById(Long id, EmployeeDto employee) {
        return null;
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employee) {
        return null;
    }

    @Override
    public void deleteEmployeeById(Long id) {

    }
}
