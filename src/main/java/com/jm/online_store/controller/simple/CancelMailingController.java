package com.jm.online_store.controller.simple;

import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.service.impl.UserServiceImpl;
import com.jm.online_store.service.interf.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/cancelMailing")
public class CancelMailingController {

    private final ConfirmationTokenRepository confirmTokenRepository;
    private final UserServiceImpl userService;
    private final ProductService productService;

    /**
     * Метод принимающий ссылку на отписку от рассылки на изменение цены
     * @param productId  id продуктa
     * @param token  продукта
     */
    @GetMapping("/{token}/{productId}")
    public String getCancelMailingPage(Model model, @PathVariable String token,
                                       @PathVariable String productId) {
        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        String email = confirmationToken.getUserEmail();
        String password = confirmationToken.getUserPassword();
        Optional<Product> product = productService.findProductById(Long.parseLong(productId));
        String productName = product.get().getProduct();

        model.addAttribute("productName", productName);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("productIdForDelete", productId);

        return "/cancelMailing";
    }
    /**
     * Метод принимающий ссылку на отписку от рассылки на изменение цены
     * @param email почта  пользователя подписанного на продукт
     * @param productId  id продукта
     */
    @PostMapping("/delete")
    public String removeProductFromMailing(@RequestParam String email, @RequestParam String password,
                                           @RequestParam String productId) {
        productService.deleteProductPriceChangeById(email, Long.parseLong(productId));
        return "/cancelMailingOK";
    }
}

