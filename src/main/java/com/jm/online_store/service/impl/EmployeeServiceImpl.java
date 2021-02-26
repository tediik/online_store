package com.jm.online_store.service.impl;

import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.EmployeeNotFoundException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.model.Employee;
import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.dto.EmployeeDto;
import com.jm.online_store.repository.EmployeeRepository;
import com.jm.online_store.service.interf.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

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
