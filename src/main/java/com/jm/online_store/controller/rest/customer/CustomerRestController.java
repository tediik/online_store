package com.jm.online_store.controller.rest.customer;

import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.exception.CustomerNotFoundException;
import com.jm.online_store.exception.ExceptionConstants;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.PasswordDto;
import com.jm.online_store.model.dto.ProductModelDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.RecentlyViewedProductsService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    /**
     * Метод получения текущего пользователя
     * @return ResponseEntity<User> возвращает текущего пользователя
     */
    @GetMapping
    @ApiOperation(value = "get current Logged in Customer",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Пользователь с идентификатором: \"id\" найден."),
    })
    public ResponseEntity<ResponseDto<UserDto>> getCurrentCustomer() {
        User currentLoggedInCustomer = customerService.getCurrentLoggedInUser();
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(currentLoggedInCustomer, UserDto.class)), HttpStatus.OK);
    }

    /**
     * rest mapping to modify user from admin page
     * @param user {@link User}
     * @return new ResponseEntity<ResponseDto<CustomerDto>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PutMapping
    @ApiOperation(value = "update Customer profile", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Cannot update customer"),
            @ApiResponse(code = 200, message = "Customer updated")
    })
    public ResponseEntity<ResponseDto<UserDto>> editCustomer(@RequestBody User user) {
        userService.updateUserProfile(user);
        log.debug("Changes to customer with id: {} was successfully added", user.getId());
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(user, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Rest mapping to  change customers mail.
     * @return ResponseEntity<ResponseDto<String>> (ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PostMapping("/changemail")
    @ApiOperation(value = "processes Customers request to change email",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "duplicatedEmailError or notValidEmailError"),
            @ApiResponse(code = 200, message = "Email will be changed after confirmation"),
    })
    public ResponseEntity<ResponseDto<String>> changeMailReq(@RequestParam String newMail) {

        Customer customer = customerService.changeMail(newMail);
        ModelMapper modelMapper = new ModelMapper();
        CustomerDto returnValue = modelMapper.map(customer, CustomerDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, "success"));
    }

    /**
     * метод обработки изменения пароля User.
     * @param passwords старый и новый пароли из PasswordDto
     * @return ResponseEntity<ResponseDto<String>>, HttpStatus.OK
     */
    @PostMapping("/change-password")
    @ApiOperation(value = "processes Customers request to change password",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "No user with such id"),
            @ApiResponse(code = 200, message = "Изменения для пользователя с идентификатором: \"id\" были успешно добавлены."),
    })
    public ResponseEntity<ResponseDto<String>> changePassword(@RequestBody PasswordDto passwords) {
        Customer customer = customerService.getCurrentLoggedInUser();
        if(passwords.getOldPassword().equals(passwords.getNewPassword())) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Старый и новый пароли совпадают."), HttpStatus.BAD_REQUEST);
        }
        if(!customerService.changePassword(customer.getId(), passwords.getOldPassword(), passwords.getNewPassword())) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Ошибка при изменении пароля."), HttpStatus.BAD_REQUEST);
        }
        log.info("Пароль для пользователя: {} успешно изменён.", customer.getEmail());
        return new ResponseEntity<>(new ResponseDto<>(true, "Изменения для пользователя с идентификатором: " + customer.getId() + " были успешно добавлены. ", ResponseOperation.NO_ERROR.getMessage()), HttpStatus.OK);
    }

    /**
     * Метод который изменяет статус пользователя при нажатии на кнопку "удалить профиль"
     * @param id идентификатор покупателя
     * @return ResponseEntity<ResponseDto<UserDto>>, HttpStatus.OK
     */
    @DeleteMapping("/deleteProfile/{id}")
    @ApiOperation(value = "Changes Users status, when Delete button clicked",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> blockProfile(@PathVariable Long id) {
        try {
            customerService.changeCustomerStatusToLocked(id);
        }
        catch (IllegalArgumentException | UserNotFoundException e) {
            log.debug("There is no user with id: {}", id);
            throw new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id));
        }
        User user = userService.findById(id).get();
        log.debug("User with id: {}, was blocked", id);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(user, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Метод который безвозвратно удаляет пользователя при нажатии на кнопку "удалить профиль" и
     * сохраняет комментарий и отзывы под сущность DeletedCustomer
     * @param id идентификатор покупателя
     * @return ResponseEntity<ResponseDto<UserDto>>, HttpStatus.OK
     */
    @DeleteMapping("/deleteProfileUnrecoverable/{id}")
    @ApiOperation(value = "Delete Users unrecoverable, when Delete button clicked",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> deleteProfileUnrecoverable(@PathVariable Long id) {
        User userToDelete;
        try {
            userToDelete = userService.findUserById(id);
            customerService.changeCustomerProfileToDeletedProfileByID(id);
            customerService.deleteByID(id);
        }
        catch (EmptyResultDataAccessException |IllegalArgumentException | UserNotFoundException e) {
            throw new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id));
        }
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(userToDelete, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Возвращает пользователя по его id
     * @param id идентификатор пользователя
     * @return ResponseEntity<ResponseDto<UserDto>> Объект User
     */
    @GetMapping("/getManagerById/{id}")
    @ApiOperation(value = "Возвращает пользователя по id",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "CUSTOMER NOT FOUND"),
    })
    public ResponseEntity<ResponseDto<UserDto>> getUserById(@PathVariable("id") Long id) {
        if (userService.findById(id).isEmpty()) {
            log.debug("User with id: {} not found", id);
            throw new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id));
        }
        log.debug("User with id: {} found", id);
        User user = userService.findUserById(id);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(user, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Метод добавляет id продукта или товара productIdFromPath в хранилище Session и
     * в базу данных
     * @param productId Product, request
     * @return ResponseEntity<ResponseDto<String>>, HttpStatus.OK
     */
    @PostMapping("/addIdProductToSessionAndToBase")
    @ApiOperation(value = "procces gives and save idProduct into Session and to DataBase",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product is saved in session")
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
     * @return ResponseEntity<ResponseDto<List<ProductModelDto>>>, HttpStatus.OK
     */
    @GetMapping("/getRecentlyViewedProductsFromDb")
    @ApiOperation(value = "procces return List<Product> from DB",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 400, message = "Request contains incorrect data")
    })
    public ResponseEntity<ResponseDto<List<ProductModelDto>>> getRecentlyViewedProducts()
            throws ResponseStatusException {
        List<ProductModelDto> recentlyViewedProducts  = recentlyViewedProductsService
                .findAllRecentlyViewedProductsByUserId(userService.getCurrentLoggedInUser().getId())
                .stream()
                .map(RecentlyViewedProducts::getProduct).map(product -> modelMapper.map(product, ProductModelDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ResponseDto<>(true, recentlyViewedProducts), HttpStatus.OK);
    }

    /**
     * Метод возвращает из базы список продуктов, которые просматривал пользователь в промежутке времени
     * @param stringStartDate - start of custom date range that receives from frontend in as String
     * @param stringEndDate   - end of custom date range that receives from frontend in as String
     * @return - ResponseEntity<ResponseDto<List<ProductModelDto>>>, HttpStatus.OK
     */
    @GetMapping("/recentlyViewedProducts")

    @ApiOperation(value = "Метод возвращает из базы список продуктов, которые просматривал пользователь" +
            "в промежутке времени (stringStartDate и stringEndDate), параметры передаются в строковом значении как 2018-10-23",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Product was not found")
    public ResponseEntity<ResponseDto<List<ProductModelDto>>> getRecentlyViewedProductsByUserIdAndDateTimeBetween(@RequestParam String stringStartDate, @RequestParam String stringEndDate) throws ResponseStatusException {
        LocalDate startDate = LocalDate.parse(stringStartDate);
        LocalDate endDate = LocalDate.parse(stringEndDate);
        List<ProductModelDto> productsViewedByUserIdAndDateTimeBetween  = recentlyViewedProductsService.findRecentlyViewedProductsByUserIdAndDateTimeBetween(userService
                .getCurrentLoggedInUser().getId(), startDate, endDate)
                .stream()
                .map(RecentlyViewedProducts::getProduct)
                .map(product -> modelMapper.map(product, ProductModelDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ResponseDto<>(true, productsViewedByUserIdAndDateTimeBetween), HttpStatus.OK);
    }

}
