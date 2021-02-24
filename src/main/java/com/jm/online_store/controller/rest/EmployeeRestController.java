package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Employee;
import com.jm.online_store.model.dto.EmployeeDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.EmployeeService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/employee")
@AllArgsConstructor
@Api(description = "Rest controller for Employee CRUD operations")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @GetMapping()
    public ResponseEntity<ResponseDto<List<EmployeeDto>>> findAllEmployees() {
        return ResponseEntity.ok(new ResponseDto<>(true, employeeService.findAllEmployees()));
    }
}
