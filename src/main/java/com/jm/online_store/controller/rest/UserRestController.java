package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.News;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
/**
 * Контроллер для работы с добавлением/изменением пользователей
 */
@RestController
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
@Slf4j
@Api(description = "Rest controller for users")
public class UserRestController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Type listType = new TypeToken<List<NewsDto>>() {}.getType();



    /**
     * Метод возвращающий список всех пользователей
     * или пустой список
     * @return List<UserDto> возвращает список всех пользователей из базы данных
     */
    @GetMapping
    @ApiOperation(value = "Get all users",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Users have been found"),
            @ApiResponse(code = 200, message = "Users have not been found. Returns empty list")
    })
    public  ResponseEntity<ResponseDto<List<UserDto>>> findAll() {
        List<UserDto> returnValue = modelMapper.map(userService.findAll(), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }



    /**
     * Метод сохраняет пользователя в базу данных
     * @param user сущность для сохранения в базе данных
     * @return ResponseEntity <UserDto> возвращает добавленную сущность с кодом ответа
     */
    @PostMapping
    @ApiOperation(value = "Save user",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponse(code = 400, message = "User has empty name or User with this name is already exists ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User has been added"),
    })
    public ResponseEntity<ResponseDto<UserDto>> saveUser(@RequestBody User user) {
        userService.addUser(user);
        UserDto returnValue = modelMapper.map(user,UserDto.class);
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.CREATED);
    }


    /**
     * Метод для обновления сущности пользователя в базе данных
     * @param user сущность для сохранения в базе данных
     * @return возвращает обновленную сущность
     */

    @PutMapping
    @ApiOperation(value = "Update users",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User has been updated"),
            @ApiResponse(code = 404, message = "User has not been found")
    })
    public ResponseEntity<ResponseDto<UserDto>> updateUser(@RequestBody User user) {
        userService.updateUserAdminPanel(user);
        UserDto returnValue = modelMapper.map(user,UserDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }



    /**
     * Метод для удаления пользователя по ID
     */
    @DeleteMapping(value = "/{userId}")
    @ApiOperation(value = "Delete user by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses({
            @ApiResponse(code = 200, message = "User deleted successfully", response = String.class),
            @ApiResponse(code = 400, message = "Something goes wrong",response = String.class)
    })
    public ResponseEntity<ResponseDto<String>> deleteUserById(@PathVariable Long userId) {
        userService.deleteByID(userId);
        return ResponseEntity.ok(new ResponseDto<>(true, String.format(
                ResponseOperation.HAS_BEEN_DELETED.getMessage(), userId)));


    }
}
