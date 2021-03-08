package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Role;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для работы с списком ролей
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Api(description = "Rest controller for roles")
public class RoleRestController {

    private final RoleService roleService;

    /**
     * Метод возвращающий список всех ролей
     *
     * @return List<UserDto> возвращает список всех пользователей из базы данных
     */
    @GetMapping
    @ApiOperation(value = "Get all roles",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponse(code = 200, message = "Roles have been found")
    public ResponseEntity<ResponseDto<List<Role>>> findAll() {
        return ResponseEntity.ok(new ResponseDto<>(true, roleService.findAll()));
    }
}
