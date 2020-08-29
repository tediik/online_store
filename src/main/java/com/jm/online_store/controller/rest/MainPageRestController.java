package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Рест контроллер главной страницы.
 */
@Controller
@RequestMapping("/")
@Slf4j
@AllArgsConstructor
public class MainPageRestController {

    @Autowired
    private UserService userService;

    private ValidationUtils validationUtils;

    private final CategoriesService categoriesService;

    @PostMapping("/registration")
    @ResponseBody
    public ResponseEntity registerUserAccount(@ModelAttribute("userForm") @Validated User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.debug("BindingResult in registerUserAccount hasErrors: {}", bindingResult);
            return new ResponseEntity("Binding error", HttpStatus.OK);
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            log.debug("Passwords do not match : passwordConfirmError");
            return new ResponseEntity("passwordError", HttpStatus.OK);
        }
        if (userService.isExist(userForm.getEmail())){
            log.debug("User with same email already exists");
            return new ResponseEntity("duplicatedEmailError", HttpStatus.OK);
        }
        if (validationUtils.isNotValidEmail(userForm.getEmail())){
            log.debug("Wrong email! Не правильно введен email");
            return new ResponseEntity("notValidEmailError", HttpStatus.OK);
        }
        userService.regNewAccount(userForm);
        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping("/activate/{token}")
    public String activate(Model model, @PathVariable String token, HttpServletRequest request) {
        userService.activateUser(token, request);
        return "redirect:/customer";
    }

    @GetMapping("/api/categories")
    public ResponseEntity<Map<String, List<String>>> getCategories() {
        List<Categories> categoriesFromDB = categoriesService.getAllCategories();
        Map<String, List<String>> categoriesBySuperCategories = new HashMap<>();
        for (Categories category : categoriesFromDB) {
            categoriesBySuperCategories.merge(category.getSuperCategory(), Arrays.asList(category.getCategory()),
                    (oldV, newV) -> Stream.concat(oldV.stream(), newV.stream()).collect(Collectors.toList()));
        }
        return new ResponseEntity<>(categoriesBySuperCategories, HttpStatus.OK);
    }
}
