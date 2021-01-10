package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.ProductService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@RestController
@RequestMapping("/customer")
@Slf4j
@Api(description = "some operations with Customer's profile")
public class CustomerRestController {
    private final CustomerService customerService;
    private final UserService userService;
    private final ProductService productService;
    private final RecentlyViewedProductsService recentlyViewedProductsService;
    private static AtomicInteger count = new AtomicInteger(0);

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
     * Метод добавляет id продукта или товара productIdFromPath в хранилище Session
     * @param recentlyViewedProducts класс сущности с тремя параметрами - время , idProduct, User
     * @return ResponseEntity<String>
     */
    @PostMapping("/addIdProductToSessionAndToBase")
    @ApiOperation(value = "procces gives and save idProduct into Session and to DataBase")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "")
    })
    public ResponseEntity<String> saveIdProductToSession(@RequestBody RecentlyViewedProducts recentlyViewedProducts, HttpServletResponse response, HttpServletRequest request)  {
        HttpSession session = request.getSession();
        int counterValue = count.intValue();
        counterValue++;
        session.setAttribute(String.valueOf(counterValue), recentlyViewedProducts.getIdProduct());
        recentlyViewedProductsService.saveRecentlyViewedProducts(recentlyViewedProducts);
        return ResponseEntity.ok("Session is set");
    }

    /**
     * Метод получает из базы коллекцию List из id продуктов или товаров productIdFromPath которые просматривал юзер
     * @param request, response - запрос и ответ
     * @return ResponseEntity<List<Product>>
     */
    @GetMapping("/getRecentlyViewedProductsFromDb")
    @ApiOperation(value = "procces return List<Product> from DB")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 400, message = "Request contains incorrect data")
    })
    public ResponseEntity<List<Product>> getRecentlyViewedProducts(HttpServletRequest request)
            throws ServletException, ResponseStatusException {
        List<Product> listProduct = new ArrayList<>();
        HttpSession session = request.getSession();
        RecentlyViewedProducts recentlyViewedProducts = (RecentlyViewedProducts) session.getAttribute("idProduct" + count.getAndIncrement());
        List<RecentlyViewedProducts> listIdProducts = recentlyViewedProductsService.findAllRecentlyViewedProductsByUserId(userService.getCurrentLoggedInUser().getId());
        for (RecentlyViewedProducts recViewProd : listIdProducts) {
            listProduct.add(productService.findProductById(Long.valueOf(recViewProd.getIdProduct())).orElse(new Product()));
        }
            return ResponseEntity.ok(listProduct);
    }
}

