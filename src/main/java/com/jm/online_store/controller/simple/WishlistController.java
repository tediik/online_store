package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    @GetMapping("/")
    public String pageWishList(Model model){
        return ("wishlist");
    }
}
