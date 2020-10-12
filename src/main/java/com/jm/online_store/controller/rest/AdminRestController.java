package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin rest controller
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/admin")
public class AdminRestController {

    private final UserService userService;

    /**
     * Rest mapping to  receive authenticated user. from admin page
     * @return ResponseEntity(authUser, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/authUser")
    public ResponseEntity<User> showAuthUserInfo() {
        User authUser = userService.getCurrentLoggedInUser();
        return new ResponseEntity<>(authUser, HttpStatus.OK);
    }

    /**
     * Rest mapping to receive all users from db. from admin page
     *
     * @return ResponseEntity(allUsers, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/allUsers")
    public ResponseEntity<List<User>> getAllUsersList() {
        List<User> allUsers = userService.findAll();
        if (allUsers.size() == 0) {
            log.debug("There are no users in db");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    /**
     * rest mapping to receive user by id from db. from admin page
     *
     * @param id - user id (Long)
     * @return ResponseEntity(user, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> getUserInfo(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            log.debug("User with id: {} not found", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        User user = userService.findById(id).get();
        log.debug("User with id: {} found, email is: {}", id, user.getEmail());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Rest mapping to delete user from db by his id from admin page
     *
     * @param id - id of User to delete {@link Long}
     * @return ResponseEntity<>(HttpStatus) {@link ResponseEntity}
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteByID(id);
        } catch (IllegalArgumentException e) {
            log.debug("There is no user with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.debug("User with id: {}, was deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * rest mapping to modify user from admin page
     *
     * @param user {@link User}
     * @return new ResponseEntity<>(HttpStatus) {@link ResponseEntity}
     */
    @PutMapping
    public ResponseEntity editUser(@RequestBody User user) {
        if (userService.findById(user.getId()).isEmpty()) {
            log.debug("There are no user with id: {}", user.getId());
            return ResponseEntity.noContent().build();
        }
        if (ValidationUtils.isNotValidEmail(user.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return ResponseEntity.badRequest().body("notValidEmailError");
        }
        if (user.getRoles().size() == 0) {
            log.debug("Roles not selected");
            return ResponseEntity.badRequest().body("emptyRolesError");
        }
        if (!userService.findById(user.getId()).get().getEmail().equals(user.getEmail())
                && userService.isExist(user.getEmail())) {
            log.debug("User with same email already exists");
            return ResponseEntity.badRequest().body("duplicatedEmailError");
        }
        userService.updateUserFromAdminPage(user);
        log.debug("Changes to user with id: {} was successfully added", user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Rest mapping to add new user from admin page
     *
     * @param newUser {@link User}
     * @return new ResponseEntity<>(String, HttpStatus) {@link ResponseEntity}
     */
    @PostMapping
    public ResponseEntity addNewUser(@RequestBody User newUser) {
        if (ValidationUtils.isNotValidEmail(newUser.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return new ResponseEntity("notValidEmailError", HttpStatus.BAD_REQUEST);
        }
        if (userService.isExist(newUser.getEmail())) {
            log.debug("User with same email already exists");
            return new ResponseEntity("duplicatedEmailError", HttpStatus.BAD_REQUEST);
        }
        if (newUser.getPassword().equals("")) {
            log.debug("Password empty");
            return new ResponseEntity("emptyPasswordError", HttpStatus.BAD_REQUEST);
        }
        if (newUser.getRoles().size() == 0) {
            log.debug("Roles not selected");
            return new ResponseEntity("emptyRolesError", HttpStatus.BAD_REQUEST);
        }
        userService.addNewUserFromAdmin(newUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Rest mapping to filter list on users by choosen role
     *
     * @param role - choosen role
     * @return List<User> filtered user's list
     */
    @PutMapping(value = "/{role}")
    public List<User> filterByRoles(@PathVariable String role) {
        return userService.findByRole(role);
    }
}
