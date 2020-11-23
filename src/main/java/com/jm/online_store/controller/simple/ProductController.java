package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.CategoriesService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/** Контроллер продукта */
@Controller
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    @GetMapping("/{id}")
    public String getProductPage() {
        return "productPage";
  }
}
