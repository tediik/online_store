package com.jm.online_store.controller.rest.customer;

import com.jm.online_store.controller.ResponseOperation;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.RecentlyViewedProductsService;
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
    @ApiOperation(value = "processes Customers request to change email",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "duplicatedEmailError or notValidEmailError"),
            @ApiResponse(code = 200, message = "Email will be changed after confirmation"),
    })
    public ResponseEntity<ResponseDto<String>> changeMailReq(@RequestParam String newMail) {
        public ResponseEntity<ResponseDto<CustomerDto>> changeMailReq(@RequestParam String newMail) {
            Customer customer = customerService.changeMail(newMail);
            return new ResponseEntity<>(new ResponseDto<>(true, "Email будет изменен после подтверждения.", ResponseOperation.NO_ERROR.getMessage()), HttpStatus.OK);
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
    @ApiOperation(value = "processes Customers request to change password",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No user with such id"),
            @ApiResponse(code = 400, message = "Wrong email or user with such email already exists"),
            @ApiResponse(code = 200, message = "Changes accepted"),
    })
    public ResponseEntity<ResponseDto<String>> changePassword(Model model,
                                         @RequestParam String oldPassword,
                                         @RequestParam String newPassword) {
        Customer customer = customerService.getCurrentLoggedInUser();
        if (customerService.findById(customer.getId()).isEmpty()) {
            log.debug("Нет пользователя с идентификатором: {}", customer.getId());
            return new ResponseEntity<>(new ResponseDto<>(false, null, "User with id " + customer.getId() + " not found"), HttpStatus.NOT_FOUND);
        }
        if (ValidationUtils.isNotValidEmail(customer.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return new ResponseEntity<>(new ResponseDto<>(false, null, "Not valid email error"), HttpStatus.BAD_REQUEST);
        }
        if (customerService.findById(customer.getId()).get().getEmail().equals(customer.getEmail())
                && customerService.isExist(customer.getEmail())) {
            log.debug("Пользователь с таким адресом электронной почты уже существует");
            return new ResponseEntity<>(new ResponseDto<>(false, null, "Duplicated email error"), HttpStatus.BAD_REQUEST);
        }
        if (customerService.changePassword(customer.getId(), oldPassword, newPassword))
            log.debug("Изменения для пользователя с идентификатором: {} были успешно добавлены", customer.getId());
        return new ResponseEntity<>(new ResponseDto<>(true, "Изменения для пользователя с идентификатором: " + customer.getId() + " были успешно добавлены. "), HttpStatus.OK);
    }

    /**
     * Метод который изменяет статус пользователя при нажатии на кнопку "удалить профиль"
     * @param id идентификатор покупателя
     * @return ResponseEntity.ok()
     */
    @DeleteMapping("/deleteProfile/{id}")
    @ApiOperation(value = "Changes Users status, when Delete button clicked",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> blockProfile(@PathVariable Long id) {
        try {
            customerService.changeCustomerStatusToLocked(id);
        }
        catch (IllegalArgumentException e) {
            log.debug("There is no user with id: {}", id);
            return new ResponseEntity<>(new ResponseDto<>(false, "User with id " + id + " not found"), HttpStatus.NOT_FOUND);
        }
        User user = userService.findById(id).get();
        log.debug("User with id: {}, was blocked", id);
        return new ResponseEntity<>(new ResponseDto<>(true, UserDto.fromUser(user)), HttpStatus.OK);
    }

    /**
     * Метод который безвозвратно удаляет пользователя при нажатии на кнопку "удалить профиль" и
     * сохраняет комментарий и отзывы под сущность DeletedCustomer
     * @param id идентификатор покупателя
     * @return ResponseEntity.ok()
     */
    @DeleteMapping("/deleteProfileUnrecoverable/{id}")
    @ApiOperation(value = "Delete Users unrecoverable, when Delete button clicked",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> deleteProfileUnrecoverable(@PathVariable Long id) {
        User userToDelete = userService.findUserById(id);
        customerService.changeCustomerProfileToDeletedProfileByID(id);
        customerService.deleteByID(id);
        return new ResponseEntity<>(new ResponseDto<>(true, UserDto.fromUser(userToDelete)), HttpStatus.OK);
    }

    /**
     * Возвращает пользователя по его id
     * @param id идентификатор пользователя
     * @return ResponseEntity<User> Объект User
     */
    @GetMapping("/getManagerById/{id}")
    @ApiOperation(value = "Возвращает пользователя по id",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User with this id not found"),
    })
    public ResponseEntity<ResponseDto<UserDto>> getUserById(@PathVariable("id") Long id) {
        if (userService.findById(id).isEmpty()) {
            log.debug("User with id: {} not found", id);
            return new ResponseEntity<>(new ResponseDto<>(false, "User with id " + id + " not found"), HttpStatus.NOT_FOUND);
        }
        log.debug("User with id: {} found", id);
        User user = userService.findUserById(id);
        return new ResponseEntity<>(new ResponseDto<>(true, UserDto.fromUser(user)), HttpStatus.OK);
    }

    /**
     * Метод добавляет id продукта или товара productIdFromPath в хранилище Session и
     * в базу данных
     * @param productId Product, request
     * @return ResponseEntity<String>
     */
    @PostMapping("/addIdProductToSessionAndToBase")
    @ApiOperation(value = "procces gives and save idProduct into Session and to DataBase",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<ResponseDto<String>> saveIdProductToSession(@RequestBody Long productId, HttpServletRequest request) {
        if (userService.getCurrentLoggedInUser() != null) {
            LocalDateTime localDateTime = LocalDateTime.now();
            HttpSession session = request.getSession();
            session.setAttribute("Product", productId);
            Long userId = userService.getCurrentLoggedInUser().getId();
            if (!recentlyViewedProductsService.ProductExistsInTableOfUserId(productId, userId)) {
                recentlyViewedProductsService.saveRecentlyViewedProducts(productId, userId, localDateTime);
            } else recentlyViewedProductsService.updateRecentlyViewedProducts(productId, userId, localDateTime);
            return new ResponseEntity<>(new ResponseDto<>(true, "Product is saved in session"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDto<>(true, "User is not authenticated. Product is not saved to session"), HttpStatus.OK);
        }
    }

    /**
     * Метод получает из базы список Продуктов, которые просматривал пользователь
     * @return ResponseEntity<List<Product>>
     */
    @GetMapping("/getRecentlyViewedProductsFromDb")
    @ApiOperation(value = "procces return List<Product> from DB",
            authorizations = { @Authorization(value = "jwtToken") })
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
            "в промежутке времени (stringStartDate и stringEndDate), параметры передаются в строковом значении как 2018-10-23",
            authorizations = { @Authorization(value = "jwtToken") })
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

