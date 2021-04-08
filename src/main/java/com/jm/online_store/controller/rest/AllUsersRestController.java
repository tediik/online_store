package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.AddressDto;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.RestoreAccountDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/api/allUsers")
@RequiredArgsConstructor
@Api(description = "Rest controller for actions with users profiles")
public class AllUsersRestController {

    private final UserService userService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Метод для получения имейла и ролей залогиненного пользователя,
     * в дальнейшем используемых на фронте при отдаче вариации страницы профиля.
     * @param authentication
     * @return
     */
    @GetMapping("/getCurrent")
    @ApiOperation(value = "Fetches email and roles of logged in user",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return new ResponseEntity<>(new ResponseDto<>(false,"No content"), HttpStatus.NO_CONTENT);
        }
        User currentUser = userService.getCurrentLoggedInUser();
        if (currentUser == null) {
            return new ResponseEntity<>(new ResponseDto<>(false,"User not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseDto<>(true,
                new UserDto()
                        .setEmail(currentUser.getEmail())
                        .setRoles(currentUser.getRoles())),
                        HttpStatus.OK);
    }

    /**
     * Метод восстановления пользователя
     * @param restoreAccountDto
     * @return
     */
    @PutMapping("/restore")
    @ApiOperation(value = "restores deleted users profile",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "User not found"),
    })
    public ResponseEntity<ResponseDto<CustomerDto>> restoreUser(@RequestBody RestoreAccountDto restoreAccountDto) {
        String msgAlert = "Пользователь не найден!";
        String email = restoreAccountDto.getEmail();
        String passwordOfClient = restoreAccountDto.getPassword();
        String passwordOfBase = userService.getPasswordByMail(email);
        try {
            if (passwordEncoder.matches(passwordOfClient, passwordOfBase)) {
                Customer customer = customerService.restoreCustomer(email);
                return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(customer, CustomerDto.class)), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ResponseDto<>(false, msgAlert), HttpStatus.BAD_REQUEST);
            }
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(new ResponseDto<>(false, msgAlert), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Rest контроллер который возвращает список юзеров из базы данных согласно номеру страницы
     * параметром запроса служит количество элементов на странице,
     * если параметр не указан выдаст 20 элементов
     * @return ResponseEntity<ResponseDto<List<UserDto>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/{pageNum}")
    @ApiOperation(value = "return list of users with page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "NO ONE USER WAS FOUND"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<List<UserDto>>> getAllUsersListPage(@PathVariable int pageNum, @RequestParam(value = "amount", required = false) Integer amount) {
        List<UserDto> allUsersDto = new ArrayList<>();
        List<User> userList = userService.findAllPage(pageNum, amount);
        userList.forEach(u -> allUsersDto.add(UserDto.fromUser(u)));

        if (allUsersDto.size() == 0) {
            log.debug("There are no users in db");
            return new ResponseEntity<>(new ResponseDto<>(true, allUsersDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseDto<>(true, allUsersDto), HttpStatus.OK);
    }
}
