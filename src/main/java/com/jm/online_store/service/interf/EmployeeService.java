package com.jm.online_store.service.interf;

import com.jm.online_store.model.Employee;
import com.jm.online_store.model.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> findAllEmployees();

    EmployeeDto findEmployeeById(Long id);

    EmployeeDto updateEmployee(EmployeeDto employee);

    EmployeeDto updateEmployeeById(Long id, EmployeeDto employee);

    EmployeeDto createEmployee(EmployeeDto employee);

    void deleteEmployeeById(Long id);



}
