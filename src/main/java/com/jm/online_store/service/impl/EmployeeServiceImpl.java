package com.jm.online_store.service.impl;

import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.EmployeeNotFoundException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.model.Employee;
import com.jm.online_store.model.dto.EmployeeDto;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.repository.EmployeeRepository;
import com.jm.online_store.service.interf.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final Type listType = new TypeToken<List<EmployeeDto>>() {}.getType();

    @Override
    public List<EmployeeDto> findAllEmployee() {
        List<Employee> employees = employeeRepository.findAll();
        return modelMapper.map(employees, listType);
    }

    @Override
    public EmployeeDto findEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(()
                -> new EmployeeNotFoundException(ExceptionEnums.EMPLOYEE.getText() + ExceptionConstants.NOT_FOUND));
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @Override
    public EmployeeDto updateEmployee(EmployeeDto employeeReq) {
        Employee employee = employeeRepository.findById(employeeReq.getId()).orElseThrow();
        Employee returnValue = employeeRepository.save(employee);
        return modelMapper.map(returnValue, EmployeeDto.class);
    }

    @Override
    public EmployeeDto updateEmployeeById(Long id, EmployeeDto employeeReq) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employee) {
        return null;
    }

    @Override
    public void deleteEmployeeById(Long id) {

    }
}
