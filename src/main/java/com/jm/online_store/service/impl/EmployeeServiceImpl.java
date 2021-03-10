package com.jm.online_store.service.impl;

import com.jm.online_store.model.Employee;
import com.jm.online_store.repository.EmployeeRepository;
import com.jm.online_store.service.interf.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee createEmployee(Employee employeeReq) {
        return employeeRepository.save(employeeReq);
    }

}
