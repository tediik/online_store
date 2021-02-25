package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.RepairOrderService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Api("Rest controller for Service center")
@RequestMapping("/api")
public class ServiceCenterRestController {

    private final UserService userService;

    /**
     * Метод для получения данных авторизованного пользователя в окне сервиса
     * @return ResponseEntity(user) возвращает пользователя
     */
    @GetMapping("/service")
    @ApiOperation(value = "Get user profile",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "User was not found"),
            @ApiResponse(code = 200, message = "User found"),
    })
    public ResponseEntity<User> getServiceProfile() {
        User user = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(user);
    }
}
