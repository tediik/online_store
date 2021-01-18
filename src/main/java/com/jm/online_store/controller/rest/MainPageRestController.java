package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.CurrentUrl;
import com.jm.online_store.model.News;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.Transliteration;
import com.jm.online_store.util.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Рест контроллер главной страницы.
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@Api(description = "Rest controller of the Main page")
public class MainPageRestController {

    private final UserService userService;
    
    @PostMapping("/registration")
    @ResponseBody
    @ApiOperation(value = "Registration of the new account")
    @ApiResponse(code = 200, message = "This response can also display error, see the comment inside it. Only \"success\" comment means that account was registered. Other options:" +
            " BindingResult in registerUserAccount hasErrors: ..., " +
            " Passwords do not match : passwordConfirmError," +
            " Passwords do not match : passwordValidError, " +
            " User with same email already exists," +
            " Wrong email! Не правильно введен email")
    public ResponseEntity registerUserAccount(@ModelAttribute("userForm") @Validated User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.debug("BindingResult in registerUserAccount hasErrors: {}", bindingResult);
            return new ResponseEntity("Binding error", HttpStatus.OK);
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            log.debug("Passwords do not match : passwordConfirmError");
            return ResponseEntity.ok("passwordError");
        }
        if (!ValidationUtils.isValidPassword(userForm.getPassword())) {
            log.debug("Passwords do not match : passwordValidError");
            return ResponseEntity.ok("passwordValidError");
        }
        if (userService.isExist(userForm.getEmail())) {
            log.debug("User with same email already exists");
            return new ResponseEntity("duplicatedEmailError", HttpStatus.OK);
        }
        if (!ValidationUtils.isValidEmail(userForm.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return ResponseEntity.ok("notValidEmailError");
        }
        userService.regNewAccount(userForm);
        return new ResponseEntity("success", HttpStatus.OK);
    }

}