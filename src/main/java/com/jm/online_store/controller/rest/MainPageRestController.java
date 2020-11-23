package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.News;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер главной страницы.
 */
@RestController
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class MainPageRestController {

	private final UserService userService;
	private final CategoriesService categoriesService;
	private final ProductService productService;
	private final StockService stockService;
	private final NewsService newsService;

	@PostMapping("/registration")
	@ResponseBody
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

	/**
	 * Метод возвращает список корневых категорий для дальнейшего формирования ссылок на подкатегории
	 */
	@GetMapping("api/categories")
	public ResponseEntity<List<Categories>> getMainCategories() {
		return ResponseEntity.ok(categoriesService.getCategoriesByParentCategoryId(0L));
	}

	/**
	 * Возвращает список первых N продуктов - N передаётся в метод сервиса .findNumProducts(N)
	 */
	@GetMapping("api/products")
	public ResponseEntity<List<Product>> getSomeProducts() {
		return ResponseEntity.ok(productService.findNumProducts(15));
	}

	/**
	 * Возвращает список опубликованных акций  - список передаётся в метод сервиса .findPublishedStocks()
	 */
	@GetMapping("api/publishedstocks")
	public ResponseEntity<List<Stock>> getPublishedStocks() {
		List<Stock> publishedStocks = stockService.findPublishedStocks();
		return ResponseEntity.ok(publishedStocks);
	}

	/**
	 * Возвращает список опубликованных новостей  - список передаётся в метод сервиса .findPublishedNews()
	 */
	@GetMapping("api/publishednews")
	public ResponseEntity<List<News>> getPublishedNews() {
		List<News> publishedNews = newsService.getAllPublished();
		return ResponseEntity.ok(publishedNews);
	}
}
