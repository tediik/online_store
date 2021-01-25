package com.jm.online_store.controller.rest.customer;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.RecentlyViewedProductsService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@RestController
@RequestMapping("/api/customer")
@Slf4j
@Api(description = "some operations with Customer's profile")
public class CustomerRestController {
    private final CustomerService customerService;
    private final UserService userService;
    private final RecentlyViewedProductsService recentlyViewedProductsService;

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
     * @param model модель для view
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
     * @param id идентификатор покупателя
     * @return ResponseEntity.ok()
     */
    @DeleteMapping("/deleteProfile/{id}")
    @ApiOperation(value = "Changes Users status, when Delete button clicked")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        customerService.changeCustomerStatusToLocked(id);
        return ResponseEntity.ok("Delete profile");
    }

    /**
     * Метод который безвозвратно удаляет пользователя при нажатии на кнопку "удалить профиль" и
     * сохраняет комментарий и отзывы под сущность DeletedCustomer
     * @param id идентификатор покупателя
     * @return ResponseEntity.ok()
     */
    @DeleteMapping("/deleteProfileUnrecoverable/{id}")
    @ApiOperation(value = "Delete Users unrecoverable, when Delete button clicked")
    public ResponseEntity<String> deleteProfileUnrecoverable(@PathVariable Long id) {
        customerService.changeCustomerProfileToDeletedProfileByID(id);
        customerService.deleteByID(id);
        return ResponseEntity.ok("Delete profile");
    }

    /**
     * Возвращает пользователя по его id
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

    /**
     * Метод добавляет id продукта или товара productIdFromPath в хранилище Session и
     * в базу данных
     * @param productId Product, request
     * @return ResponseEntity<String>
     */
    @PostMapping("/addIdProductToSessionAndToBase")
    @ApiOperation(value = "procces gives and save idProduct into Session and to DataBase")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<String> saveIdProductToSession(@RequestBody Long productId, HttpServletRequest request) {
        LocalDateTime localDateTime = LocalDateTime.now();
        HttpSession session = request.getSession();
        session.setAttribute("Product", productId);
        Long userId = userService.getCurrentLoggedInUser().getId();
        if (!recentlyViewedProductsService.ProductExistsInTableOfUserId(productId, userId)) {
            recentlyViewedProductsService.saveRecentlyViewedProducts(productId, userId, localDateTime);
        } else recentlyViewedProductsService.updateRecentlyViewedProducts(productId, userId, localDateTime);
        return ResponseEntity.ok("Product is saved in session");
    }

    /**
     * Метод получает из базы список Продуктов, которые просматривал пользователь
     * @return ResponseEntity<List<Product>>
     */
    @GetMapping("/getRecentlyViewedProductsFromDb")
    @ApiOperation(value = "procces return List<Product> from DB")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 400, message = "Request contains incorrect data")
    })
    public ResponseEntity<List<Product>> getRecentlyViewedProducts()
            throws ResponseStatusException {
        return ResponseEntity.ok(recentlyViewedProductsService
                .findAllRecentlyViewedProductsByUserId(userService
                        .getCurrentLoggedInUser().getId()).stream()
                .map(RecentlyViewedProducts::getProduct)
                .collect(Collectors.toList()));
    }

    /**
     * Метод возвращает из базы список продуктов, которые просматривал пользователь в промежутке времени
     * @param stringStartDate - start of custom date range that receives from frontend in as String
     * @param stringEndDate   - end of custom date range that receives from frontend in as String
     * @return - ResponseEntity<List<Product>>
     */
    @GetMapping("/recentlyViewedProducts")
    @ApiOperation(value = "Метод возвращает из базы список продуктов, которые просматривал пользователь" +
                "в промежутке времени (stringStartDate и stringEndDate), параметры передаются в строковом значении как 2018-10-23")
    @ApiResponse(code = 404, message = "Product was not found")
    public ResponseEntity<List<Product>> getRecentlyViewedProductsByUserIdAndDateTimeBetween(@RequestParam String stringStartDate, @RequestParam String stringEndDate) throws ResponseStatusException {
        LocalDate startDate = LocalDate.parse(stringStartDate);
        LocalDate endDate = LocalDate.parse(stringEndDate);
        return ResponseEntity.ok(recentlyViewedProductsService.findRecentlyViewedProductsByUserIdAndDateTimeBetween(userService
                .getCurrentLoggedInUser().getId(), startDate, endDate).stream()
                .map(RecentlyViewedProducts::getProduct)
                .collect(Collectors.toList()));
    }
}

