package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
@RequiredArgsConstructor
@Slf4j
@Api(description = "Rest controller for users")
public class UserRestController {

    private final UserService userService;

    @GetMapping
    @ApiOperation(value = "Get all users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @ApiOperation(value = "Save user")
    public User saveUser(@RequestBody User user) {
        userService.addUser(user);
        return user;
    }

    @PutMapping
    @ApiOperation(value = "Update users")
    public User updateUser(@RequestBody User user) {
        userService.updateUserAdminPanel(user);
        return user;
    }

    @DeleteMapping(value = "/{userId}")
    @ApiOperation(value = "Delete user by ID")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        userService.deleteByID(userId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Метод который возвращает юзера по его id
     * @param id юзера
     * @return ResponseEntity<User>
     */
    @GetMapping("/getManagerById/{id}")
    @ApiOperation(value = "Find user-manager by id and return user-manager")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User with this id not found"),
    })
    public ResponseEntity<User> getUserById(@PathVariable ("id") Long id) {
        if (userService.findById(id).isEmpty()) {
            log.debug("User with id: {} not found", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.debug("User with id: {} found", id);
        User user = userService.findUserById(id);
        //User user = userService.findById(id).get();
        if (user.getFirstName() == null) {
            user.setFirstName("");
        }
        if (user.getLastName() == null) {
            user.setLastName("");
        }
        return ResponseEntity.ok(user);
    }
}
