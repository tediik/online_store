package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.dto.EmployeeDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.EmployeeService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/employee")
@AllArgsConstructor
@Api(description = "Rest controller for Employee CRUD operations")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<EmployeeDto>>> findAllEmployees() {
        return ResponseEntity.ok(new ResponseDto<>(true, employeeService.findAllEmployees()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<EmployeeDto>> findEmployeeById(@PathVariable Long id){
        return ResponseEntity.ok(new ResponseDto<>(true, employeeService.findEmployeeById(id)));
    }

    @PutMapping
    public ResponseEntity<ResponseDto<EmployeeDto>> updateEmployee(@RequestBody EmployeeDto employeeReq) {
        EmployeeDto returnValue = employeeService.updateEmployee(employeeReq);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<String>> deleteEmployeeById(@PathVariable Long id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(new ResponseDto<>(true, ResponseOperation.HAS_BEEN_DELETED.getMessage(),
                ResponseOperation.NO_ERROR.getMessage()));
    }

//    @PostMapping()
//    public ResponseEntity<ResponseDto<EmployeeDto>> createEmployee(@RequestBody EmployeeDto employeeReq){
//        EmployeeDto returnValue = employeeService.createEmployee(employeeReq);
//        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
//    }
}
