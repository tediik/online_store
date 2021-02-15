package com.jm.online_store.controller.rest.admin;

import com.jm.online_store.controller.ResponseOperation;
import com.jm.online_store.exception.admin.UserExceptionConstants;
import com.jm.online_store.exception.admin.UserServiceException;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Admin rest controller
 */
@Slf4j
@AllArgsConstructor
@RestController
@Api(value = "Rest controller for actions from admins page")
@RequestMapping(value = "/api/admin")
public class AdminRestController {

    private final UserService userService;

    private final FavouritesGroupService favouritesGroupService;

    private final CommonSettingsService commonSettingsService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Rest mapping to  receive authenticated user. from admin page
     * @return ResponseEntity<ResponseDto<UserDto>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/authUser")
    @ApiOperation(value = "receive authenticated user. from admin page", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> showAuthUserInfo() {
        User authUser = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(new ResponseDto<>(true, modelMapper.map(authUser, UserDto.class)));
    }

    /**
     * Rest mapping to receive all users from db. from admin page
     * @return ResponseEntity<ResponseDto<List<UserDto>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/allUsers")
    @ApiOperation(value = "return list of users", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "NO ONE USER WAS FOUND"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<List<UserDto>>> getAllUsersList() {
        List<UserDto> allUsersDto = new ArrayList<>();
        for (User user: userService.findAll()){
            allUsersDto.add(modelMapper.map(user, UserDto.class));
        }
        if (allUsersDto.size() == 0) {
            log.debug("There are no users in db");
            return new ResponseEntity<>(new ResponseDto<>(true, allUsersDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseDto<>(true, allUsersDto), HttpStatus.OK);
    }

    /**
     * rest mapping to receive user by id from db. from admin page
     * @param id - user id (Long)
     * @return ResponseEntity<ResponseDto<UserDto>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/users/{id}")
    @ApiOperation(value = "receive user by id from db. from admin page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "User not found"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<UserDto>> getUserInfo(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            log.debug("User with id: {} not found", id);
            throw new UserServiceException(UserExceptionConstants.USER_NOT_FOUND);
        }
        User user = userService.findById(id).get();
        log.debug("User with id: {} found, email is: {}", id, user.getEmail());
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(user, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Rest mapping to delete user from db by his id from admin page
     * @param id - id of User to delete {@link Long}
     * @return ResponseEntity<ResponseDto<UserDto>>(userToDelete, HttpStatus) {@link ResponseEntity}
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "delete user from db by his id from admin page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "USER NOT FOUND"),
            @ApiResponse(code = 200, message = ""),
    })
    public ResponseEntity<ResponseDto<UserDto>> deleteUser(@PathVariable Long id) {
        User userToDelete;
        try {
            userToDelete = new User(id, userService.findUserById(id).getEmail());
            userService.deleteByID(id);
        } catch (IllegalArgumentException | EmptyResultDataAccessException | NullPointerException e) {
            log.debug("There is no user with id: {}", id);
            throw new UserServiceException(UserExceptionConstants.USER_NOT_FOUND);
        }
        log.debug("User with id: {}, was deleted successfully", id);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(userToDelete, UserDto.class)), HttpStatus.OK);
    }

    /**
     * rest mapping to modify user from admin page
     * @param user {@link User}
     * @return new ResponseEntity<ResponseDto>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PutMapping
    @ApiOperation(value = "modify user from admin page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "EMAIL ADDRESS IS NOT VALID / NO ROLES SELECTED / USER WITH SAME EMAIL ALREADY EXISTS / USER NOT FOUND"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<UserDto>> editUser(@RequestBody User user) {
        if (userService.findById(user.getId()).isEmpty()) {
            log.debug("There are no user with id: {}", user.getId());
            throw new UserServiceException(UserExceptionConstants.USER_NOT_FOUND);
        }
        if (ValidationUtils.isNotValidEmail(user.getEmail())) {
            log.debug("Wrong email");
            throw new UserServiceException(UserExceptionConstants.EMAIL_NOT_VALID);
        }
        if (user.getRoles().size() == 0) {
            log.debug("Roles not selected");
            throw new UserServiceException(UserExceptionConstants.EMPTY_ROLES_ERROR);
        }
        if (!userService.findById(user.getId()).get().getEmail().equals(user.getEmail())
                && userService.isExist(user.getEmail())) {
            log.debug("User with same email already exists");
            throw new UserServiceException(UserExceptionConstants.EMAIL_ALREADY_EXISTS);
        }
        userService.updateUserFromAdminPage(user);
        log.debug("Changes to user with id: {} was successfully added", user.getId());
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(user, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Rest mapping to add new user from admin page
     * @param newUser {@link User}
     * @return new ResponseEntity<ResponseDto>(UserDto user, HttpStatus) {@link ResponseEntity}
     */
    @PostMapping
    @ApiOperation(value = "add new user from admin page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 409, message = "User with same email already exists"),
            @ApiResponse(code = 400, message = "EMAIL ADDRESS IS NOT VALID / EMAIL ADDRESS IS NOT VALID / PASSWORD COLUMN IS EMPTY / NO ROLES SELECTED"),
    })
    public ResponseEntity<ResponseDto<UserDto>> addNewUser(@RequestBody User newUser) {
        if (ValidationUtils.isNotValidEmail(newUser.getEmail())) {
            log.debug("Wrong email");
            throw new UserServiceException(UserExceptionConstants.EMAIL_NOT_VALID);
        }
        if (userService.isExist(newUser.getEmail())) {
            log.debug("User with same email already exists");
            throw new UserServiceException(UserExceptionConstants.EMAIL_ALREADY_EXISTS);
        }
        if (newUser.getPassword().equals("")) {
            log.debug("Password is empty");
            throw new UserServiceException(UserExceptionConstants.EMPTY_PASSWORD_ERROR);
        }
        if (newUser.getRoles().size() == 0) {
            log.debug("Roles not selected");
            throw new UserServiceException(UserExceptionConstants.EMPTY_ROLES_ERROR);
        }
        userService.addNewUserFromAdmin(newUser);
        User customer = userService.findByEmail(newUser.getEmail()).get();
        FavouritesGroup favouritesGroup = new FavouritesGroup();
        favouritesGroup.setName("Все товары");
        favouritesGroup.setUser(customer);
        favouritesGroupService.save(favouritesGroup);
        userService.updateUser(customer);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(newUser, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Rest mapping to filter list on users by choosen role
     * @param role - choosen role
     * @return ResponseEntity<ResponseDto<List<UserDto>>>(ResponseDto, HttpStatus) filtered user's list
     */
    @PutMapping(value = "/{role}")
    @ApiOperation(value = "filter list on users by chosen role", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "NO ONE USER WAS FOUND"),
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<List<UserDto>>> filterByRoles(@PathVariable String role) {
        List<UserDto> allUsersWithRoleDto = new ArrayList<>();
        for (User user: userService.findByRole(role)){
            allUsersWithRoleDto.add(modelMapper.map(user, UserDto.class));
        }
        if (allUsersWithRoleDto.size() == 0) {
            log.debug("There are no users with chosen role in db");
            throw new UserServiceException(UserExceptionConstants.EMPTY_USERS_LIST);
        }
        return new ResponseEntity<>(new ResponseDto<>(true, allUsersWithRoleDto), HttpStatus.OK);
    }

    /**
     * Метод для изменения наименования магазина
     * @param commonSettings настройки, содержащие название магазина
     * @return String, ResponseEntity.ok()
     */
    @ApiOperation(value = "edit store name", authorizations = { @Authorization(value="jwtToken") })
    @PutMapping(value = "/editStoreName")
    public ResponseEntity<ResponseDto<String>> editStoreName(CommonSettings commonSettings){
        commonSettingsService.updateTextValue(commonSettings);
        return new ResponseEntity<>(new ResponseDto<>(true, "Store name was changed", ResponseOperation.NO_ERROR.getMessage()), HttpStatus.OK);
    }
}
