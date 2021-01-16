package com.jm.online_store.controller.rest.customer;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/customer")
@Slf4j
@Api(description = "some operations with Customer's profile")
public class CustomerRestController {
    private final CustomerService customerService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReviewService reviewService;

    @PostMapping("/changemail")
    @ApiOperation(value = "processes Customers request to change email")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "duplicatedEmailError or notValidEmailError"),
            @ApiResponse(code = 200, message = "Email will be changed after confirmation"),
    })
    public ResponseEntity<String> changeMailReq(@RequestParam String newMail) {
        Customer customer = customerService.getCurrentLoggedInUser();
        if (customerService.isExist(newMail)) {
            log.debug("Попытка ввести дублирующийся email: " + newMail);
            return new ResponseEntity("duplicatedEmailError", HttpStatus.BAD_REQUEST);
        }
        if (ValidationUtils.isNotValidEmail(newMail)) {
            return new ResponseEntity("notValidEmailError", HttpStatus.BAD_REQUEST);
        } else {
            userService.changeUsersMail(customer, newMail);
            return ResponseEntity.ok("Email будет изменен после подтверждения.");
        }
    }

    /**
     * метод обработки изменения пароля User.
     *
     * @param model       модель для view
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @return страница User
     */
    @PostMapping("/change-password")
    @ApiOperation(value = "processes Customers request to change email")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No user with such id"),
            @ApiResponse(code = 400, message = "Wrong email or user with such email already exists"),
            @ApiResponse(code = 200, message = "Changes accepted"),
    })
    public ResponseEntity changePassword(Model model,
                                         @RequestParam String oldPassword,
                                         @RequestParam String newPassword) {
        Customer customer = customerService.getCurrentLoggedInUser();
        if (customerService.findById(customer.getId()).isEmpty()) {
            log.debug("Нет пользователя с идентификатором: {}", customer.getId());
            return ResponseEntity.noContent().build();
        }
        if (ValidationUtils.isNotValidEmail(customer.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return ResponseEntity.badRequest().body("notValidEmailError");
        }
        if (customerService.findById(customer.getId()).get().getEmail().equals(customer.getEmail())
                && customerService.isExist(customer.getEmail())) {
            log.debug("Пользователь с таким адресом электронной почты уже существует");
            return ResponseEntity.badRequest().body("duplicatedEmailError");
        }
        if (customerService.changePassword(customer.getId(), oldPassword, newPassword))
            log.debug("Изменения для пользователя с идентификатором: {} был успешно добавлен", customer.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Метод который изменяет статус пользователя при нажатии на кнопку "удалить профиль"
     *
     * @param id идентификатор покупателя
     * @return ResponseEntity.ok()
     */
    @DeleteMapping("/deleteProfile/{id}")
    @ApiOperation(value = "Changes Users status, when Delete button clicked")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        customerService.changeCustomerStatusToLocked(id);
        return ResponseEntity.ok("Delete profile");
    }

    @DeleteMapping("/deleteProfile2/{id}")
    @ApiOperation(value = "Changes Users status, when Delete button clicked")
    public ResponseEntity<String> deleteProfile2(@PathVariable Long id) {
        User customer = userService.findUserById(id);
        List<Comment> customerComments = commentService.findAllByCustomer(customer);
        List<Review> customerReview = reviewService.findAllByCustomer(customer);
        User deletedUser = userService.findUserByEmail("deleted@mail.ru");
        for (Comment comment : customerComments) comment.setCustomer(deletedUser);
        for (Review review : customerReview) review.setCustomer(deletedUser);
        customerService.deleteByID(id);
        return ResponseEntity.ok("Delete profile");
    }

    /**
     * Возвращает пользователя по его id
     *
     * @param id идентификатор пользователя
     * @return ResponseEntity<User> Объект User
     */
    @GetMapping("/getManagerById/{id}")
    @ApiOperation(value = "Возвращает пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User with this id not found"),
    })
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        if (userService.findById(id).isEmpty()) {
            log.debug("User with id: {} not found", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.debug("User with id: {} found", id);
        User user = userService.findUserById(id);
        if (user.getFirstName() == null) {
            user.setFirstName("");
        }
        if (user.getLastName() == null) {
            user.setLastName("");
        }
        return ResponseEntity.ok(user);
    }
}
